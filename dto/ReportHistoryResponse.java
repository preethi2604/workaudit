package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.ReportHistory;
import com.workaudit.workaudit.entity.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public class ReportHistoryResponse {

    private UUID id;

    private UUID reportId;

    private ReportStatus previousStatus;

    private ReportStatus newStatus;

    private String reason;

    private UUID changedBy;

    private Instant changedAt;

    public ReportHistoryResponse() {
    }

    public ReportHistoryResponse(
            UUID id,
            UUID reportId,
            ReportStatus previousStatus,
            ReportStatus newStatus,
            String reason,
            UUID changedBy,
            Instant changedAt
    ) {
        this.id = id;
        this.reportId = reportId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public static ReportHistoryResponse fromEntity(
            ReportHistory history
    ) {

        return new ReportHistoryResponse(
                history.getId(),
                history.getReport().getId(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getReason(),
                history.getChangedBy(),
                history.getChangedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getReportId() {
        return reportId;
    }

    public ReportStatus getPreviousStatus() {
        return previousStatus;
    }

    public ReportStatus getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}