/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.leave.integration;

import com.hr.leave.domain.enums.Status;
import com.hr.leave.domain.model.Leave;
import com.hr.leave.domain.repository.LeaveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author saddam.benhadja
 */
@Testcontainers
@DataJpaTest
class LeaveRepositoryTest {

    @Autowired
    private LeaveRepository repository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("leave_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveLeave() {

        UUID employeeId = UUID.randomUUID();

        Leave leave = Leave.builder()
                .employeeId(employeeId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .reason("Vacation")
                .status(Status.PENDING)
                .build();

        Leave saved = repository.save(leave);

        assertNotNull(saved.getId());
        assertEquals(Status.PENDING, saved.getStatus());
    }

    @Test
    void shouldFindByEmployeeId() {

        UUID employeeId = UUID.randomUUID();

        Leave leave = Leave.builder()
                .employeeId(employeeId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .reason("Sick")
                .status(Status.PENDING)
                .build();

        repository.save(leave);

        List<Leave> result = repository.findByEmployeeId(employeeId);

        assertEquals(1, result.size());
        assertEquals(employeeId, result.get(0).getEmployeeId());
    }

    @Test
    void shouldCheckExistingPendingLeave() {

        UUID employeeId = UUID.randomUUID();

        Leave leave = Leave.builder()
                .employeeId(employeeId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .reason("Test")
                .status(Status.PENDING)
                .build();

        repository.save(leave);

        boolean exists = repository.existsByEmployeeIdAndStatus(employeeId, Status.PENDING);

        assertTrue(exists);
    }
}
