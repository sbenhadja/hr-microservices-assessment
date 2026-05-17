/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.persistance;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * @author saddam.benhadja
 */
@Entity
@Table(name = "employee_projection", schema = "hr_leave")
@Data
public class EmployeeProjection {

  @Id
  @Column(name = "employee_id")
  private UUID employeeId;

  @Column(name = "status", nullable = false)
  private String status;
}
