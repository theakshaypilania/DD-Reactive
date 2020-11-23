package com.mmt.ddreactive.controller;

import com.mmt.ddreactive.LoggingHelper;
import com.mmt.ddreactive.MetricUtil;
import com.mmt.ddreactive.pojo.Dimension;
import com.mmt.ddreactive.pojo.Experiment;
import com.mmt.ddreactive.service.DDService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;


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
    MDC.clear();
    MDC.put("correlationKey", correlationKey);
    return ddService.getExperimentData(expId)
                    .doOnSuccess(s -> metricUtil.addToCounter("success", "", "", "", 1))
                    .doOnError(e -> metricUtil.addToCounter("failure", "", "", "", 1));
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

  @GetMapping(value = "/saveRedis")
  public Mono<Boolean> save(@RequestParam("key") String key, @RequestParam("value") String value) {
    return ddService.saveInRedis(key, value);
  }

  @GetMapping(value = "/getRedis")
  public Mono<String> get(@RequestParam("key") String key) {
    return ddService.get(key);
  }

}
