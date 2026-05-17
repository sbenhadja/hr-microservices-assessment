package com.hr.employee.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String email,
    String department,
    String status) {}
