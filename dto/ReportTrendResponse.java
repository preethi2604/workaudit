package com.workaudit.workaudit.dto;

import java.time.LocalDate;

public class ReportTrendResponse {

    private LocalDate date;

    private long totalReports;
    private long submittedReports;
    private long underReviewReports;
    private long approvedReports;
    private long rejectedReports;
    private long validationFailedReports;

    public ReportTrendResponse() {
    }

    public ReportTrendResponse(
            LocalDate date,
            long totalReports,
            long submittedReports,
            long underReviewReports,
            long approvedReports,
            long rejectedReports,
            long validationFailedReports
    ) {
        this.date = date;
        this.totalReports = totalReports;
        this.submittedReports = submittedReports;
        this.underReviewReports = underReviewReports;
        this.approvedReports = approvedReports;
        this.rejectedReports = rejectedReports;
        this.validationFailedReports = validationFailedReports;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public long getSubmittedReports() {
        return submittedReports;
    }

    public long getUnderReviewReports() {
        return underReviewReports;
    }

    public long getApprovedReports() {
        return approvedReports;
    }

    public long getRejectedReports() {
        return rejectedReports;
    }

    public long getValidationFailedReports() {
        return validationFailedReports;
    }
}