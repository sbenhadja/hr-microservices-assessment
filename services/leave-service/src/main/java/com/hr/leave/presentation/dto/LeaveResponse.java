package com.hr.leave.presentation.dto;

public record LeaveResponse(
    String id, String employeeId, String startDate, String endDate, String reason, String status) {}
