package com.workaudit.workaudit.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_history")
public class ReportHistory {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private DailyReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 30)
    private ReportStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private ReportStatus newStatus;

    @Column(length = 1000)
    private String reason;

    /**
     * UUID of the employee/user who caused the change.
     * SYSTEM changes can use null until authentication is implemented.
     */
    @Column(name = "changed_by")
    private UUID changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    public ReportHistory() {
    }

    public ReportHistory(
            DailyReport report,
            ReportStatus previousStatus,
            ReportStatus newStatus,
            String reason,
            UUID changedBy
    ) {
        this.id = UUID.randomUUID();
        this.report = report;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.changedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DailyReport getReport() {
        return report;
    }

    public void setReport(DailyReport report) {
        this.report = report;
    }

    public ReportStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ReportStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ReportStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ReportStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }
}