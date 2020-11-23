package com.mmt.ddreactive;

import java.util.HashMap;

import com.mmt.lib.MetricManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MetricUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(MetricUtil.class);
  @Autowired
  private MetricManager metricManager;

  @Async
  public void addToCounter(String controller, String dependencyLayer, String errorCode, String metricType, int count) {
    HashMap<String, String> metricTags = generateMeterName(controller, dependencyLayer, errorCode, metricType);
    if (!metricTags.isEmpty()) {
      metricManager.logCounter(metricTags, count);
    }

  }

  private HashMap<String, String> generateMeterName(String controller, String dependencyLayer, String errorCode, String metricType) {
    HashMap<String, String> metricTags = new HashMap<>();
    metricTags.put("CONTROLLER_TAG", controller);
    metricTags.put("ERROR_CODE_TAG", errorCode);
    metricTags.put("DEPENDENCY_TAG", dependencyLayer);
    metricTags.put("ERROR_TYPE_TAG", metricType);
    return metricTags;
  }

}
