package com.hr.leave.infrastructure.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void createLeave(String product) {
    log.info("Sending leave event to Kafka");
    kafkaTemplate.send("leave-events", product);
  }
}
