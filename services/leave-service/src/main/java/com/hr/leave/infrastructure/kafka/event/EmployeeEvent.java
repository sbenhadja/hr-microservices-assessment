/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.kafka.event;

import java.util.UUID;

/**
 * @author saddam.benhadja
 */
public record EmployeeEvent(UUID eventId, UUID employeeId, String status, String eventType) {}
