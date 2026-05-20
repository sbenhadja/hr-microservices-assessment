package com.hr.employee.application.service;

import com.hr.employee.presentation.dto.EmployeeCreateRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {

  EmployeeResponse createEmployee(EmployeeCreateRequest employeeRequest);

  EmployeeResponse updateEmployee(UUID id, EmployeeCreateRequest employeeRequest);

  EmployeeResponse getEmployeeById(UUID id);

  List<EmployeeResponse> getAllEmployees();

  EmployeeResponse deactivateEmployee(UUID id);
}
