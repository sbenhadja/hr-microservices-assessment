package com.hr.leave.presentation.controller;

import com.hr.leave.application.service.LeaveService;
import com.hr.leave.infrastructure.kafka.producer.LeaveProducer;
import com.hr.leave.presentation.dto.LeaveRequest;
import com.hr.leave.presentation.dto.LeaveResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaves")
@Slf4j
@RequiredArgsConstructor
public class LeaveController {

  private final LeaveService service;
  private final LeaveProducer producer;

  @PostMapping
  public LeaveResponse create(@RequestBody LeaveRequest request) {
    System.out.println(request);
    LeaveResponse response = service.createLeave(request);
    producer.createLeave(request.employeeId().toString());
    log.info("HELLO FROM LEAVES SERVICE");
    return response;
  }

  @GetMapping
  public List<LeaveResponse> retriveByEmployee(@RequestParam UUID employeeId) {
    return service.getLeavesByEmployee(employeeId);
  }

  @PutMapping("/{leaveId}")
  public LeaveResponse updateLeaveStatus(@PathVariable UUID leaveId, @RequestParam String status) {
    return service.updateLeaveStatus(leaveId, status);
  }
}
