package com.hr.employee.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeCreateRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    String email,
    UUID departmentId,
    String status,
    @NotNull LocalDate recrutementDate) {}
