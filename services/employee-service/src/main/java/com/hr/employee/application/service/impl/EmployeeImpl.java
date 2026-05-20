package com.hr.employee.application.service.impl;

import com.hr.employee.application.mapper.EmployeeMapper;
import com.hr.employee.application.service.EmployeeService;
import com.hr.employee.domain.enums.Status;
import com.hr.employee.domain.model.Department;
import com.hr.employee.domain.model.Employee;
import com.hr.employee.domain.repository.DepartmentRepository;
import com.hr.employee.domain.repository.EmployeeRepository;
import com.hr.employee.infrastructure.kafka.producer.EmployeeProducer;
import com.hr.employee.presentation.dto.EmployeeCreateRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;
import com.hr.employee.presentation.exception.AlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeImpl implements EmployeeService {

  private final EmployeeRepository empRepository;
  private final DepartmentRepository depRepository;
  private final EmployeeProducer producer;

  public EmployeeImpl(
      EmployeeRepository empRepository,
      EmployeeProducer producer,
      DepartmentRepository depRepository) {
    this.empRepository = empRepository;
    this.producer = producer;
    this.depRepository = depRepository;
  }

  @Override
  public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
    if (empRepository.existsByEmail(request.email())) {
      throw new AlreadyExistsException("Email already exists");
    }
    Department department =
        depRepository
            .findById(request.departmentId())
            .orElseThrow(() -> new RuntimeException("Department not found"));

    log.info("Department from DB: id={}, name={}", department.getId(), department.getName());
    System.out.println("Before EmployeeMapper.toEntity");
    Employee employee = EmployeeMapper.toEntity(request, department);
    System.out.println("EmployeeMapper.toEntity done");
    Employee saved = empRepository.save(employee);
    System.out.println("Employee save done");
    producer.publishCreated(saved);
    System.out.println("Producer done");

    return EmployeeMapper.toresponse(saved);
  }

  @Override
  public EmployeeResponse updateEmployee(UUID id, EmployeeCreateRequest request) {
    Employee employee =
        empRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    Department department =
        depRepository
            .findById(request.departmentId())
            .orElseThrow(() -> new RuntimeException("Department not found"));

    EmployeeMapper.toUpdate(employee, department, request);
    Employee updated = empRepository.save(employee);

    producer.publishUpdated(updated);

    return EmployeeMapper.toresponse(updated);
  }

  @Override
  public EmployeeResponse getEmployeeById(UUID id) {
    Employee employee =
        empRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    return EmployeeMapper.toresponse(employee);
  }

  @Override
  public List<EmployeeResponse> getAllEmployees() {
    List<Employee> listEmployee = empRepository.findAll();
    if (listEmployee.isEmpty()) {
      throw new EntityNotFoundException("No Employee found");
    }

    return listEmployee.stream().map(employee -> EmployeeMapper.toresponse(employee)).toList();
  }

  @Override
  public EmployeeResponse deactivateEmployee(UUID id) {
    Employee employee =
        empRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    employee.setStatus(Status.INACTIVE);
    producer.publishDesactivated(employee);
    empRepository.save(employee);

    return EmployeeMapper.toresponse(employee);
  }
}
