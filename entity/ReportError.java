package com.workaudit.workaudit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_errors")
public class ReportError {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private DailyReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false, length = 40)
    private ReportErrorType errorType;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ReportError() {
    }

    public ReportError(
            DailyReport report,
            ReportErrorType errorType,
            String message
    ) {
        this.id = UUID.randomUUID();
        this.report = report;
        this.errorType = errorType;
        this.message = message;
    }

    @PrePersist
    protected void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public DailyReport getReport() {
        return report;
    }

    public ReportErrorType getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}