package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public class ReviewReportResponse {

    private UUID reportId;
    private ReportStatus previousStatus;
    private ReportStatus currentStatus;
    private String reason;
    private UUID reviewedBy;
    private Instant reviewedAt;

    public ReviewReportResponse() {
    }

    public ReviewReportResponse(
            UUID reportId,
            ReportStatus previousStatus,
            ReportStatus currentStatus,
            String reason,
            UUID reviewedBy,
            Instant reviewedAt
    ) {
        this.reportId = reportId;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.reason = reason;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }

    public UUID getReportId() {
        return reportId;
    }

    public ReportStatus getPreviousStatus() {
        return previousStatus;
    }

    public ReportStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getReason() {
        return reason;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }
}