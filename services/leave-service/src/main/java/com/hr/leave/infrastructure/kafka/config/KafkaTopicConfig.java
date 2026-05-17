/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author saddam.benhadja
 */
@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic employeeEventsTopic() {
    return TopicBuilder.name("employee.events").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic employeeEventsDlqTopic() {
    return TopicBuilder.name("employee.events.DLT").partitions(3).replicas(1).build();
  }
}
