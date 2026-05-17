/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author saddam.benhadja
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "processed_event", schema = "hr_leave")
public class ProcessedEvent {

  @Id private UUID eventId;

  private Instant processedAt;
}
