/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.employee.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Data;

/**
 * @author saddam.benhadja
 */
@Entity
@Data
public class Department {
  @Id @GeneratedValue private UUID id;

  private String name;

  //    @OneToMany(mappedBy = "department")
  //    private List<Employee> employees = new ArrayList<>();

  public Department() {}

  public Department(UUID id, String name) {
    this.id = id;
    this.name = name;
  }
}
