/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.infrastructure.idempotency;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author saddam.benhadja
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {}
