package com.mmt.ddreactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmt.ddreactive.CacheUtil;
import com.mmt.ddreactive.LoggingHelper;
import com.mmt.ddreactive.model.ExperimentTbl;
import com.mmt.ddreactive.pojo.Dimension;
import com.mmt.ddreactive.pojo.Experiment;
import com.mmt.ddreactive.pojo.Policy;
import com.mmt.ddreactive.protoOut.CheckProto;
import com.mmt.ddreactive.repository.ExperimentRepository;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Service;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class DDService {
  private static Map<Integer, Experiment> expMap = new HashMap<>();

  private static Map<Integer, CheckProto.Course> protoMap = new HashMap<>();
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  CacheUtil cacheUtil;
  //  @Autowired
  //  RedisAdvancedClusterReactiveCommands<String, String> redis;
  @Autowired
  private ReactiveStringRedisTemplate redisTemplate;
  @Autowired
  private KafkaService kafkaService;
  @Autowired
  private ExperimentRepository experimentRepository;
  @Value("${str}")
  private String str;

  @PostConstruct
  public void init() {
    Dimension d = new Dimension();
    d.setName("test1");
    d.setValue(30);
    Dimension e = new Dimension();
    e.setName("test2");
    e.setValue(100);
    Dimension f = new Dimension();
    f.setName("test3");
    f.setValue(40);
    Dimension g = new Dimension();
    g.setName("test4");
    g.setValue(50);

    Experiment exp = new Experiment();
    exp.setId(6);
    exp.setDimList(Arrays.asList(d, e));
    expMap.put(6, exp);

    exp = new Experiment();
    exp.setId(35);
    exp.setDimList(Arrays.asList(d, e, f));
    expMap.put(35, exp);

    exp = new Experiment();
    exp.setId(36);
    exp.setDimList(Arrays.asList(d, e, g));
    expMap.put(36, exp);

    protoMap.put(1, CheckProto.Course.newBuilder().setCourseName("testcourse").setId(1).addStudent(
        CheckProto.Student.newBuilder().setFirstName("testFirstName").setId(2).setLastName("testLastName").build())
                                     .build());
  }

  public Mono<Optional<Experiment>> getExperimentData(int expId) {
    return Mono.just(Optional.ofNullable(expMap.get(expId))).flatMap(a -> {
      if (!a.isPresent()) {
        return Mono.error(new RuntimeException());
      }
      return Mono.just(a);
    });
  }

  public Flux<Dimension> getAllDimensions(int expId) {
    return Flux.fromIterable(expMap.getOrDefault(expId, expMap.get(6)).getDimList())
               .doOnEach(LoggingHelper
                             .logOnNext(a -> log.info("Dimensions Present in exp::{}, dim::{}", expId, a.getName())));
  }

  public Mono<String> saveExperiment(Experiment experiment) {
    if (experiment.getId() == 0) {
      return Mono.just("failure");
    }
    expMap.put(experiment.getId(), experiment);
    return Mono.just("Success");
  }

  //    public Mono<Boolean> saveInRedis(String key, String value) {
  //      return redisTemplate.opsForValue().set(key, value);
  //    }


  public Mono<Map<String, Policy>> get(String key) {
    long start = System.currentTimeMillis();
    return redisTemplate.opsForValue().get(key).map(a -> {
      try {
        return objectMapper.readValue(a, new TypeReference<Map<String, Policy>>() {
        });
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return null;
      }
    }).name("akshay").tag("aks", "hay").metrics().doOnSuccess(a -> {
      log.error("time: {}", System.currentTimeMillis() - start);
    });

    //    return redis.get(key).map(a -> {
    //      try {
    //        return objectMapper.readValue(a, new TypeReference<Map<String, Policy>>() {
    //        });
    //      } catch (JsonProcessingException e) {
    //        e.printStackTrace();
    //        return null;
    //      }
    //    });
  }


  public Mono<Map<String, Policy>> getPolicyObjFromCacheFromJedis(String key) {
    long start = System.currentTimeMillis();
//    Flux.just("","","");
    Mono.just("").map(a-> {
      try {
        Thread.sleep(4);
        return a;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return Mono.just(a);
    }).subscribeOn(Schedulers.boundedElastic());

//    return Mono.fromCallable(() -> cacheUtil.get(key)).subscribeOn(Schedulers.boundedElastic()).map(a -> {
//      try {
//        return objectMapper.readValue(a, new TypeReference<Map<String, Policy>>() {
//        });
//      } catch (JsonProcessingException e) {
//        e.printStackTrace();
//        return null;
//      }
//    }).doOnSuccess( a -> {
//      log.error("time: {}", System.currentTimeMillis() - start);
//    });
  }

  public void putToKafka(Dimension dimension) {
    kafkaService.sendMessages(dimension.getName(), String.valueOf(dimension.getValue()));
  }

  public Flux<ExperimentTbl> findAllExperiments() {
    return experimentRepository.findAll();
  }

  public Flux<ExperimentTbl> findAllExperimentsWithIds(List<Integer> expIds) {
    return experimentRepository.findAllByIds(expIds);
  }

  public Mono<CheckProto.Course> getProto(int id) {
    return Mono.just(protoMap.get(id));
  }
}
