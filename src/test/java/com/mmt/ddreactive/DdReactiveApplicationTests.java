package com.mmt.ddreactive;

import com.mmt.ddreactive.controller.DDController;
import com.mmt.ddreactive.protoOut.CheckProto;
import com.mmt.ddreactive.repository.ExperimentRepository;
import com.mmt.ddreactive.service.DDService;
import com.mmt.ddreactive.service.KafkaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;

@AutoConfigureWebClient
@WebFluxTest(controllers = DDController.class)
@Import(DDService.class)
class DdReactiveApplicationTests {

  @Autowired
  private WebTestClient webClient;

  @MockBean
  private ReactiveRedisTemplate<String, String> redisTemplate;

  @MockBean
  private KafkaService kafkaService;

  @MockBean
  private ExperimentRepository experimentRepository;

  @MockBean
  private MetricUtil metricUtil;

  @Test
  void contextLoads() {
  }

  @Test
  public void protoTest() {
    BlockHound.install();
    CheckProto.Course course = webClient.get()
                                        .uri(uriBuilder -> uriBuilder.path("/getProto").queryParam("id", 1).build())
                                        .attribute("id", 1).exchange().expectStatus().isOk()
                                        .expectBody(CheckProto.Course.class).returnResult().getResponseBody();
    System.out.println(course);

  }

}
