package com.hr.employee.presentation.dto;

public record EmployeeResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    DepartmentResponse department,
    String status,
    String recrutementDate) {}
