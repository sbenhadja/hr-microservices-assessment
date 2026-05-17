/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.kafka.config;

import com.hr.leave.infrastructure.kafka.event.EmployeeEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * @author saddam.benhadja
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;
  private final DefaultErrorHandler kafkaErrorHandler;

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ConsumerFactory<String, EmployeeEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

    // Kafka broker address
    //        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    //        props.put(ConsumerConfig.GROUP_ID_CONFIG, "leave-service");
    //        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    // Wrap deserializers with error handling to avoid crashing on bad messages
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

    // Actual key deserializer: message keys are plain strings
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

    // Actual value deserializer: message values are JSON
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    // Ignore the __TypeId__ header set by the producer
    // (avoids ClassNotFoundException when producer/consumer are different services)
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    // Since type headers are ignored, deserialize all messages into this local class
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EmployeeEvent.class);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  //    @Bean
  //    public ProducerFactory<String, Object> producerFactory() {
  //        Map<String, Object> config = new HashMap<>();
  //        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
  //        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  //        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
  //        return new DefaultKafkaProducerFactory<>(config);
  //    }
  //
  //    @Bean
  //    public KafkaTemplate<String, Object> kafkaTemplate() {
  //        return new KafkaTemplate<>(producerFactory());
  //    }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent>
      kafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory());
    factory.setCommonErrorHandler(kafkaErrorHandler);

    //        factory.setCommonErrorHandler(
    //                new DefaultErrorHandler(
    //                        new DeadLetterPublishingRecoverer(kafkaTemplate()), // DLQ
    //                        new FixedBackOff(2000L, 3) // RETRY 3 times, 2s interval
    //                )
    //        );

    return factory;
  }
}
