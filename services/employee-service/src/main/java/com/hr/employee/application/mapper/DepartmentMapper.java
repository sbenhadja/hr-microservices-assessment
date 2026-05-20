/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.employee.application.mapper;

import com.hr.employee.domain.model.Department;
import com.hr.employee.presentation.dto.DepartmentRequest;
import com.hr.employee.presentation.dto.DepartmentResponse;

/**
 * @author saddam.benhadja
 */
public class DepartmentMapper {

  public static DepartmentResponse toresponse(Department d) {
    return new DepartmentResponse(d.getId(), d.getName());
  }

  public static Department toEntity(DepartmentRequest request) {
    return new Department(request.id(), request.name());
  }
}
