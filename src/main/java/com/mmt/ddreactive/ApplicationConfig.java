package com.mmt.ddreactive;

import com.mmt.lib.MetricManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApplicationConfig {

  @Bean(name = "metricManager")
  public MetricManager getMetricManager() {
    return new MetricManager("DD-Reactive", 5, true, true, "test-version");
  }

  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    List<String> list = new ArrayList<>();
    list.add("172.16.47.79:7000");
    return new LettuceConnectionFactory(new RedisClusterConfiguration(list));
  }

}
