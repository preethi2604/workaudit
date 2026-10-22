package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.EmployeeRole;

import java.time.Instant;
import java.util.UUID;

public class EmployeeResponse {

    private UUID id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeRole role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            UUID id,
            String employeeCode,
            String firstName,
            String lastName,
            String email,
            EmployeeRole role,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmployeeResponse fromEntity(
            Employee employee
    ) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getRole(),
                employee.isActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}