/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.application.service.usecase;

import com.hr.leave.application.service.LeaveService;
import com.hr.leave.domain.enums.Status;
import com.hr.leave.domain.model.Leave;
import com.hr.leave.domain.repository.LeaveRepository;
import com.hr.leave.infrastructure.persistance.EmployeeProjectionRepository;
import com.hr.leave.presentation.dto.LeaveRequest;
import com.hr.leave.presentation.dto.LeaveResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author saddam.benhadja
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveUserCase implements LeaveService {

  private final LeaveRepository repository;
  private final EmployeeProjectionRepository employeeProjectionRepository;

  // Create leave
  @Override
  public LeaveResponse createLeave(LeaveRequest request) {

    validateEmployee(request.employeeId());
    // check if existPendingLeaveByEmployeeId
    if (repository.existsByEmployeeIdAndStatus(request.employeeId(), Status.PENDING)) {
      throw new IllegalStateException("Employee already has a pending leave request");
    }
    Leave leave =
        Leave.builder()
            .employeeId(request.employeeId())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .reason(request.reason())
            .status(Status.PENDING)
            .build();
    Leave savedLeave = repository.save(leave);
    return new LeaveResponse(
        savedLeave.getId().toString(),
        savedLeave.getEmployeeId().toString(),
        savedLeave.getStartDate().toString(),
        savedLeave.getEndDate().toString(),
        savedLeave.getReason(),
        savedLeave.getStatus().name());
  }

  // Retrieve leaves by employee
  @Override
  public List<LeaveResponse> getLeavesByEmployee(UUID employeeId) {

    List<Leave> leaves = repository.findByEmployeeId(employeeId);
    return leaves.stream()
        .map(
            leave ->
                new LeaveResponse(
                    leave.getId().toString(),
                    leave.getEmployeeId().toString(),
                    leave.getStartDate().toString(),
                    leave.getEndDate().toString(),
                    leave.getReason(),
                    leave.getStatus().name()))
        .collect(Collectors.toList());
  }

  // Update leave status (PENDING / APPROVED / REJECTED)
  @Override
  public LeaveResponse updateLeaveStatus(UUID leaveId, String status) {

    Status parsedStatus;
    try {
      // two (02) birds with one stone: validation + parsing
      parsedStatus = Status.valueOf(status);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid status value");
    }

    Leave leave =
        repository
            .findById(leaveId)
            .orElseThrow(() -> new EntityNotFoundException("Leave not found"));

    leave.setStatus(parsedStatus);

    Leave updatedLeave = repository.save(leave);

    return new LeaveResponse(
        updatedLeave.getId().toString(),
        updatedLeave.getEmployeeId().toString(),
        updatedLeave.getStartDate().toString(),
        updatedLeave.getEndDate().toString(),
        updatedLeave.getReason(),
        updatedLeave.getStatus().name());
  }

  // local validator
  private void validateEmployee(UUID employeeId) {
    /**
     * We can here retrieve the employee from employeeProjection using its id and ensure the
     * employee exists and is in ACTIVE state before proceeding.
     */
    boolean exists = employeeProjectionRepository.existsById(employeeId);
    if (!exists) {
      throw new EntityNotFoundException("Employee does not exist");
    }
  }
}
