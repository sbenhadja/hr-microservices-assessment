/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.kafka.consumer;

import com.hr.leave.infrastructure.idempotency.ProcessedEvent;
import com.hr.leave.infrastructure.idempotency.ProcessedEventRepository;
import com.hr.leave.infrastructure.kafka.event.EmployeeEvent;
import com.hr.leave.infrastructure.persistance.EmployeeProjection;
import com.hr.leave.infrastructure.persistance.EmployeeProjectionRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author saddam.benhadja
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeConsumer {

  private final EmployeeProjectionRepository employeeProjectionRepository;
  private final ProcessedEventRepository processedEventRepository;

  @KafkaListener(topics = "employee.events", groupId = "leave-service")
  public void consume(EmployeeEvent event) {

    System.out.println("RECEIVED EVENT");
    // Insure idempotency
    if (processedEventRepository.existsById(event.eventId())) {
      log.info("Event already processed: {}", event.eventId());
      return;
    }
    System.out.println("PROCESSING RECEIVED EVENT");
    processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
    System.out.println("RECEIVED EVENT PROCESSED");
    //        try {
    //            // Insure idempotency and two birds with one stone again :)
    //            processedEventRepository.save(new ProcessedEvent(event.enventId(),
    // Instant.now()));
    //        } catch (DataIntegrityViolationException ex) {
    //            log.info("Already processed event");
    //            return; // already processed
    //        }
    // Continue working
    try {
      switch (event.eventType()) {
        case "CREATED" -> {
          EmployeeProjection projection = new EmployeeProjection();
          projection.setEmployeeId(event.employeeId());
          projection.setStatus("ACTIVE");

          employeeProjectionRepository.save(projection);
        }

        case "UPDATED" -> {
          employeeProjectionRepository
              .findById(event.employeeId())
              .ifPresent(
                  projection -> {
                    projection.setStatus(event.status());
                    employeeProjectionRepository.save(projection);
                  });
        }

        case "DESACTIVATED" -> {
          employeeProjectionRepository.deleteById(event.employeeId());
        }
      }
    } catch (Exception e) {
      log.error("❌ Error processing event: {}", event, e);
      throw e; // IMPORTANT → triggers retry + DLQ
    }
  }
}
