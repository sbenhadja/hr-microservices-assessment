package com.hr.employee.infrastructure.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LeaveConsumer {

  @KafkaListener(topics = "leave-events", groupId = "employee-group")
  public void consume(String message) {
    log.info("Received leave event", message);
    System.out.println("Received leave event: " + message);
  }
}
