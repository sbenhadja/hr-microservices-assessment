package com.hr.employee.application.service;

import com.hr.employee.presentation.dto.EmployeeRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {

  EmployeeResponse createEmployee(EmployeeRequest employeeRequest);

  EmployeeResponse updateEmployee(UUID id, EmployeeRequest employeeRequest);

  EmployeeResponse getEmployeeById(UUID id);

  List<EmployeeResponse> getAllEmployees();

  void deactivateEmployee(UUID id);
}
