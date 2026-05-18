/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.unite;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hr.leave.application.service.usecase.LeaveUserCase;
import com.hr.leave.domain.enums.Status;
import com.hr.leave.domain.model.Leave;
import com.hr.leave.domain.repository.LeaveRepository;
import com.hr.leave.infrastructure.persistance.EmployeeProjectionRepository;
import com.hr.leave.presentation.dto.LeaveRequest;
import com.hr.leave.presentation.dto.LeaveResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * @author saddam.benhadja
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LeaveUserCaseTest {

  @Mock private LeaveRepository repository;

  @Mock private EmployeeProjectionRepository employeeProjectionRepository;

  @InjectMocks private LeaveUserCase service;

  private UUID employeeId;
  private UUID leaveId;
  private LeaveRequest request;

  @BeforeEach
  void setUp() {
    employeeId = UUID.randomUUID();
    leaveId = UUID.randomUUID();

    request =
        new LeaveRequest(employeeId, LocalDate.now(), LocalDate.now().plusDays(3), "vacation");
  }

  // CREATE LEAVE
  @Test
  void shouldCreateLeaveSuccessfully() {
    // given
    when(employeeProjectionRepository.existsById(employeeId)).thenReturn(true);
    when(repository.existsByEmployeeIdAndStatus(employeeId, Status.PENDING)).thenReturn(false);

    Leave saved =
        Leave.builder()
            .id(leaveId)
            .employeeId(employeeId)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .reason(request.reason())
            .status(Status.PENDING)
            .build();

    when(repository.save(any(Leave.class))).thenReturn(saved);

    // when
    LeaveResponse response = service.createLeave(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.employeeId()).isEqualTo(employeeId.toString());
    assertThat(response.status()).isEqualTo("PENDING");

    verify(repository).save(any(Leave.class));
  }

  @Test
  void shouldThrowWhenEmployeeDoesNotExist() {
    // given
    when(employeeProjectionRepository.existsById(employeeId)).thenReturn(false);

    // when / then
    assertThatThrownBy(() -> service.createLeave(request))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Employee does not exist");
  }

  @Test
  void shouldThrowWhenPendingLeaveAlreadyExists() {
    // given
    when(employeeProjectionRepository.existsById(employeeId)).thenReturn(true);
    when(repository.existsByEmployeeIdAndStatus(employeeId, Status.PENDING)).thenReturn(true);

    // when / then
    assertThatThrownBy(() -> service.createLeave(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Employee already has a pending leave request");
  }

  // GET LEAVES
  @Test
  void shouldReturnLeavesByEmployee() {
    // given
    Leave leave =
        Leave.builder()
            .id(leaveId)
            .employeeId(employeeId)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2))
            .reason("vacation")
            .status(Status.PENDING)
            .build();

    when(repository.findByEmployeeId(employeeId)).thenReturn(List.of(leave));

    // when
    List<LeaveResponse> result = service.getLeavesByEmployee(employeeId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).employeeId()).isEqualTo(employeeId.toString());

    verify(repository).findByEmployeeId(employeeId);
  }

  // UPDATE STATUS
  @Test
  void shouldUpdateLeaveStatusSuccessfully() {
    // given
    Leave leave =
        Leave.builder()
            .id(leaveId)
            .employeeId(employeeId)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2))
            .reason("vacation")
            .status(Status.PENDING)
            .build();

    when(repository.findById(leaveId)).thenReturn(Optional.of(leave));
    when(repository.save(any(Leave.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    LeaveResponse response = service.updateLeaveStatus(leaveId, "APPROVED");

    // then
    assertThat(response.status()).isEqualTo("APPROVED");

    verify(repository).save(any(Leave.class));
  }

  @Test
  void shouldThrowWhenLeaveNotFound() {
    // given
    when(repository.findById(leaveId)).thenReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> service.updateLeaveStatus(leaveId, "APPROVED"))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Leave not found");
  }

  @Test
  void shouldThrowWhenStatusInvalid() {
    // given
    when(repository.findById(leaveId))
        .thenReturn(
            Optional.of(
                Leave.builder().id(leaveId).employeeId(employeeId).status(Status.PENDING).build()));

    // when / then
    assertThatThrownBy(() -> service.updateLeaveStatus(leaveId, "INVALID"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid status value");
  }
}
