package com.hr.employee.presentation.controller;

import com.hr.employee.application.service.EmployeeService;
import com.hr.employee.presentation.dto.EmployeeRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @PostMapping
  public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest request) {
    EmployeeResponse response = employeeService.createEmployee(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
    List<EmployeeResponse> responses = employeeService.getAllEmployees();
    return new ResponseEntity<>(responses, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
    EmployeeResponse response = employeeService.getEmployeeById(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeResponse> updateEmployee(
      @PathVariable UUID id, @RequestBody EmployeeRequest request) {
    EmployeeResponse response = employeeService.updateEmployee(id, request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivateEmployee(@PathVariable UUID id) {
    employeeService.deactivateEmployee(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
