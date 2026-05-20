/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.employee.application.mapper;

import com.hr.employee.domain.enums.Status;
import com.hr.employee.domain.model.Department;
import com.hr.employee.domain.model.Employee;
import com.hr.employee.presentation.dto.EmployeeCreateRequest;
import com.hr.employee.presentation.dto.EmployeePatchRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;

/**
 * @author saddam.benhadja
 */
public class EmployeeMapper {

  public static EmployeeResponse toresponse(Employee e) {
    return new EmployeeResponse(
        e.getId().toString(),
        e.getFirstName(),
        e.getLastName(),
        e.getEmail(),
        DepartmentMapper.toresponse(e.getDepartment()),
        e.getStatus().name(),
        e.getRecrutementDate().toString());
  }

  public static Employee toEntity(EmployeeCreateRequest request, Department dep) {
    return new Employee(
        request.firstName(), request.lastName(), request.email(), dep, request.recrutementDate());
  }

  public static Employee toUpdate(Employee e, Department d, EmployeeCreateRequest request) {
    e.setFirstName(request.firstName());
    e.setLastName(request.lastName());
    e.setEmail(request.email() != null ? request.email() : "");
    e.setDepartment(d);
    e.setStatus(Status.valueOf(request.status()));
    return e;
  }

  public static Employee patch(Employee e, EmployeePatchRequest req) {
    if (req.firstName() != null) e.setFirstName(req.firstName());
    if (req.lastName() != null) e.setLastName(req.lastName());
    if (req.email() != null) e.setEmail(req.email());
    if (req.department() != null) e.setDepartment(DepartmentMapper.toEntity(req.department()));
    if (req.status() != null) e.setStatus(Status.valueOf(req.status()));
    if (req.recrutementDate() != null) e.setRecrutementDate(req.recrutementDate());

    return e;
  }
}
