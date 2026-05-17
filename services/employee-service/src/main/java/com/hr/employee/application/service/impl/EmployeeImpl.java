package com.hr.employee.application.service.impl;

import com.hr.employee.application.service.EmployeeService;
import com.hr.employee.domain.enums.Status;
import com.hr.employee.domain.model.Employee;
import com.hr.employee.domain.repository.EmployeeRepository;
import com.hr.employee.infrastructure.kafka.producer.EmployeeProducer;
import com.hr.employee.presentation.dto.EmployeeRequest;
import com.hr.employee.presentation.dto.EmployeeResponse;
import com.hr.employee.presentation.exception.AlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmployeeImpl implements EmployeeService {

  private final EmployeeRepository repository;
  private final EmployeeProducer producer;

  public EmployeeImpl(EmployeeRepository repository, EmployeeProducer producer) {
    this.repository = repository;
    this.producer = producer;
  }

  @Override
  public EmployeeResponse createEmployee(EmployeeRequest request) {
    if (repository.existsByEmail(request.email())) {
      throw new AlreadyExistsException("Email already exists");
    }
    Employee employee =
        new Employee(
            request.firstName(), request.lastName(), request.email(), request.department());

    Employee saved = repository.save(employee);

    producer.publishCreated(saved);

    return new EmployeeResponse(
        saved.getId().toString(),
        saved.getFirstName(),
        saved.getLastName(),
        saved.getEmail(),
        saved.getDepartement(),
        saved.getStatus().name());
  }

  @Override
  public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
    Employee employee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    employee.setFirstName(request.firstName());
    employee.setLastName(request.lastName());
    employee.setEmail(request.email());
    employee.setDepartement(request.department());
    employee.setStatus(Status.valueOf(request.status()));

    Employee updated = repository.save(employee);

    producer.publishUpdated(updated);

    return new EmployeeResponse(
        updated.getId().toString(),
        updated.getFirstName(),
        updated.getLastName(),
        updated.getEmail(),
        updated.getDepartement(),
        updated.getStatus().name());
  }

  @Override
  public EmployeeResponse getEmployeeById(UUID id) {
    Employee employee_ =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    Employee employee = employee_;

    return new EmployeeResponse(
        employee.getId().toString(),
        employee.getFirstName(),
        employee.getLastName(),
        employee.getEmail(),
        employee.getDepartement(),
        employee.getStatus().name());
  }

  @Override
  public List<EmployeeResponse> getAllEmployees() {
    List<Employee> listEmployee = repository.findAll();
    if (listEmployee.isEmpty()) {
      throw new EntityNotFoundException("No Employee found");
    }
    return listEmployee.stream()
        .map(
            employee ->
                new EmployeeResponse(
                    employee.getId().toString(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getDepartement(),
                    employee.getStatus().name()))
        .toList();
  }

  @Override
  public void deactivateEmployee(UUID id) {
    Employee employee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    employee.setStatus(Status.INACTIVE);
    producer.publishDesactivated(employee);
    repository.save(employee);
  }
}
