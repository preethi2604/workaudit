package com.workaudit.workaudit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "employee_domains",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_domain",
                        columnNames = {
                                "employee_id",
                                "domain_id"
                        }
                )
        }
)
public class EmployeeDomain {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false
    )
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "domain_id",
            nullable = false
    )
    private Domain domain;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(nullable = false)
    private boolean active = true;

    public EmployeeDomain() {
    }

    public EmployeeDomain(
            Employee employee,
            Domain domain
    ) {
        this.id = UUID.randomUUID();
        this.employee = employee;
        this.domain = domain;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (assignedAt == null) {
            assignedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Domain getDomain() {
        return domain;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}