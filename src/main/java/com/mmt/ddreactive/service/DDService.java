package com.mmt.ddreactive.service;

import com.mmt.ddreactive.LoggingHelper;
import com.mmt.ddreactive.controller.DDController;
import com.mmt.ddreactive.pojo.Dimension;
import com.mmt.ddreactive.pojo.Experiment;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class DDService {
  private static Map<Integer, Experiment> expMap = new HashMap<>();

  @Autowired
  private ReactiveRedisTemplate<String, String> redisTemplate;

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
  }

  public Mono<Experiment> getExperimentData(int expId) {
    return Mono.justOrEmpty(expMap.get(expId)).defaultIfEmpty(new Experiment());
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

  public Mono<Boolean> saveInRedis(String key, String value) {
    return redisTemplate.opsForValue().set(key, value);
  }

  public Mono<String> get(String key) {
    return redisTemplate.opsForValue().get(key);
  }
}
