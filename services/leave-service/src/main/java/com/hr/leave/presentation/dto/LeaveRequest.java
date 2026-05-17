package com.hr.leave.presentation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveRequest(
    UUID employeeId, LocalDate startDate, LocalDate endDate, String reason) {}
