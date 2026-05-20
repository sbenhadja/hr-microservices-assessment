package com.hr.employee.presentation.dto;

import java.time.LocalDate;

public record EmployeePatchRequest(
    String firstName,
    String lastName,
    String email,
    DepartmentRequest department,
    String status,
    LocalDate recrutementDate) {}
