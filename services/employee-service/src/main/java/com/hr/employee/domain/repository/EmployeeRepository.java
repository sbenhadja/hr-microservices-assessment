package com.hr.employee.domain.repository;

import com.hr.employee.domain.model.Employee;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

  public boolean existsByEmail(String email);
}
