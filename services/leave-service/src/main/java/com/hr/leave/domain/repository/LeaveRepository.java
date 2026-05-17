/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.domain.repository;

import com.hr.leave.domain.enums.Status;
import com.hr.leave.domain.model.Leave;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author saddam.benhadja
 */
@Repository
public interface LeaveRepository extends JpaRepository<Leave, UUID> {

  public List<Leave> findByEmployeeId(UUID employeeId);

  boolean existsByEmployeeIdAndStatus(UUID employeeId, Status status);
}
