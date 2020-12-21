package com.mmt.ddreactive.service;

import com.mmt.ddreactive.LoggingHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.util.context.Context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaService {
  private static final String TOPIC = "Hello-Kafka";
  private static final String BOOTSTRAP_SERVERS = "localhost:9092";
  private static final String CLIENT_ID_CONFIG = "DD-Reactive";

  private KafkaSender<String, String> sender = null;
  private SimpleDateFormat dateFormat = null;

  @PostConstruct
  public void init() {

    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    SenderOptions<String, String> senderOptions = SenderOptions.create(props);
//    senderOptions.scheduler(Schedulers.elastic());
    sender = KafkaSender.create(senderOptions);

    dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
  }

  @PreDestroy
  public void close() {
    sender.close();
  }

  public void sendMessages(String key, String value) {
    String corr = MDC.get("correlationKey");
    sender.send(Flux.just(SenderRecord.create(new ProducerRecord<>(TOPIC, key, value), value))).log()
          .doOnError(e -> log.error("Send failed::{}", e.getMessage()))
          .subscribe(data -> {
            RecordMetadata metadata = data.recordMetadata();
            Mono.just("").doOnEach(LoggingHelper
                                       .logOnNext(
                                           b -> log.info(
                                               "Message::{} sent successfully, topic-partition={}-{} offset={} timestamp={}",
                                               data.correlationMetadata(), metadata.topic(), metadata.partition(),
                                               metadata.offset(),
                                               dateFormat.format(new Date(metadata.timestamp()))))
                                  ).contextWrite(Context.of("correlationKey", corr)).subscribe();


          });
  }
}

