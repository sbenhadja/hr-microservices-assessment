package com.hr.leave.application.service;

import com.hr.leave.presentation.dto.LeaveRequest;
import com.hr.leave.presentation.dto.LeaveResponse;
import java.util.List;
import java.util.UUID;

public interface LeaveService {

  LeaveResponse createLeave(LeaveRequest request);

  List<LeaveResponse> getLeavesByEmployee(UUID employeeId);

  LeaveResponse updateLeaveStatus(UUID leaveId, String status);
}
