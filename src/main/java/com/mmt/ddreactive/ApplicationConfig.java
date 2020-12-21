package com.mmt.ddreactive;

import com.mmt.lib.MetricManager;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ApplicationConfig {

  @Bean(name = "metricManager")
  public MetricManager getMetricManager() {
    return new MetricManager("DD-Reactive", 5, false, true, "test-version");
  }

  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    List<String> list = new ArrayList<>();
    list.add("172.16.47.79:7000");
    return new LettuceConnectionFactory(new RedisClusterConfiguration(list));
  }

//    @Bean
//    public RedisAdvancedClusterReactiveCommands<String, String> getRedis() {
//      RedisClusterClient redisClient = RedisClusterClient.create("redis://172.16.47.79:7000");
//      redisClient.setOptions(ClusterClientOptions.builder().publishOnScheduler(true).build());
//      return redisClient.connect().reactive();
//    }

  //  @Bean
  //  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
  //    return new ProtobufHttpMessageConverter();
  //  }
  //
  //  @Bean
  //  RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
  //    return new RestTemplate(Arrays.asList(hmc));
  //  }

}
