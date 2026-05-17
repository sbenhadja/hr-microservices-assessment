/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.kafka.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * @author saddam.benhadja
 */
@Configuration
@RequiredArgsConstructor
public class KafkaRetryConfig {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Bean
  public DefaultErrorHandler kafkaErrorHandler() {

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

    return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3));
  }
}
