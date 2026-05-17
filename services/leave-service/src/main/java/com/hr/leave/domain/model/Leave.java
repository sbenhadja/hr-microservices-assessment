/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.domain.model;

import com.hr.leave.domain.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

/**
 * @author saddam.benhadja
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Leave {

  @Id @GeneratedValue @UuidGenerator private UUID id;
  private UUID employeeId;
  private LocalDate startDate;
  private LocalDate endDate;
  private String reason;

  @Enumerated(EnumType.STRING)
  private Status status;
}
