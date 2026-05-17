/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.employee.infrastructure.kafka.producer;

import com.hr.employee.domain.model.Employee;
import com.hr.employee.infrastructure.kafka.event.EmployeeEvent;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author saddam.benhadja
 */
@Service
public class EmployeeProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public EmployeeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  private static final String TOPIC = "employee.events";

  public void publishCreated(Employee employee) {

    EmployeeEvent event =
        new EmployeeEvent(
            UUID.randomUUID(), employee.getId(), employee.getStatus().name(), "CREATED");

    kafkaTemplate.send(TOPIC, employee.getId().toString(), event);
  }

  public void publishUpdated(Employee employee) {

    EmployeeEvent event =
        new EmployeeEvent(
            UUID.randomUUID(), employee.getId(), employee.getStatus().name(), "UPDATED");

    kafkaTemplate.send(TOPIC, employee.getId().toString(), event);
  }

  public void publishDesactivated(Employee employee) {

    EmployeeEvent event =
        new EmployeeEvent(
            UUID.randomUUID(), employee.getId(), employee.getStatus().name(), "DESACTIVATED");

    kafkaTemplate.send(TOPIC, employee.getId().toString(), event);
  }
}
