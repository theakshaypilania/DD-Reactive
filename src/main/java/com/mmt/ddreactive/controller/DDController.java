package com.mmt.ddreactive.controller;

import com.mmt.ddreactive.LoggingHelper;
import com.mmt.ddreactive.MetricUtil;
import com.mmt.ddreactive.model.ExperimentTbl;
import com.mmt.ddreactive.pojo.Dimension;
import com.mmt.ddreactive.pojo.Experiment;
import com.mmt.ddreactive.pojo.Policy;
import com.mmt.ddreactive.protoOut.CheckProto;
import com.mmt.ddreactive.service.DDService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class DDController {

  @Autowired
  DDService ddService;

  @Autowired
  MetricUtil metricUtil;

  @GetMapping(value = "/getExperiment/{expId}")
  public Mono<Experiment> getDimensionForExperiment(@PathVariable("expId") int expId, @RequestHeader(
      "Correlation-Key") String correlationKey) {
    MDC.put("correlationKey", correlationKey);
    return ddService.getExperimentData(expId).filter(Optional::isPresent).map(Optional::get)
                    .doOnSuccess(s -> {
                      metricUtil.addToCounter("success", "", "", "", 1);
                      log.info("success");
                    })
                    .doOnError(e -> {
                      metricUtil.addToCounter("failure", "", "", "", 1);
                      log.info("failure");
                    }).onErrorResume(e -> Mono.empty());
  }

  @GetMapping(value = "/getAllDimensions/{expId}")
  public Flux<Dimension> getAllDimensions(@PathVariable("expId") int expId, @RequestHeader(
      "Correlation-Key") String apiId) {
    return Mono.just(String.format("hi just checking::%s", expId)).doOnEach(LoggingHelper.logOnNext(log::info))
               .thenMany(ddService.getAllDimensions(expId))
               .delayElements(Duration.ofSeconds(2))
               .doOnEach(LoggingHelper.logOnNext(
                   a -> log.info("Data fetched for exp::{} and dimension-name::{}", expId, a.getName())))
               .contextWrite(Context.of("correlationKey", apiId));
  }

  @PostMapping(value = "/saveExperiment")
  public Mono<String> saveExperiment(@RequestBody Experiment experiment) {
    return ddService.saveExperiment(experiment);
  }

  //  @GetMapping(value = "/saveRedis")
  //  public Mono<Boolean> save(@RequestParam("key") String key, @RequestParam("value") String value) {
  //    return ddService.saveInRedis(key, value);
  //  }

  @GetMapping(value = "/getPolicyObjL")
  public Mono<Map<String, Policy>> get(@RequestParam("key") String key) {
    //    BlockHound.install();
//    return ddService.get(key);
    Thread.getAllStackTraces()
          .keySet()
          .stream()
          .collect(Collectors.toList());
    return null;
  }


    @GetMapping(value = "/getPolicyObjJ")
    public Mono<Map<String, Policy>> getPolicyObjJedis(@RequestParam("key") String key) {
    BlockHound.install();
      return ddService.getPolicyObjFromCacheFromJedis(key);
    }

  @PostMapping(value = "/putKafka")
  public void putInKafka(@RequestBody Dimension dimension, @RequestHeader(
      "Correlation-Key") String correlationKey) {
    BlockHound.install();
    MDC.put("correlationKey", correlationKey);
    ddService.putToKafka(dimension);
  }

  @GetMapping(value = "/getAllExpFromDB")
  public Flux<ExperimentTbl> getAllExperimentsFromDB(@RequestHeader("Correlation-Key") String correlationKey) {
    BlockHound.install();
    return ddService.findAllExperiments();
  }

  @GetMapping(value = "/getExpWithIds")
  public Flux<ExperimentTbl> getExperimentsFromIds(@RequestParam("expList") List<Integer> expList) {
    //    BlockHound.install();
    return ddService.findAllExperimentsWithIds(expList);
  }

  @GetMapping(value = "/getProto", produces = "application/x-protobuf")
  public Mono<CheckProto.Course> getProto(@RequestParam("id") int id) {
    //    BlockHound.install();
    return ddService.getProto(id);
  }
}
