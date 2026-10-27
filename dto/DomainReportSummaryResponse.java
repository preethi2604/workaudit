package com.workaudit.workaudit.dto;

import java.time.LocalDate;
import java.util.UUID;

public class DomainReportSummaryResponse {

    private UUID domainId;
    private String domainName;
    private String domainCode;

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalReports;
    private long submittedReports;
    private long underReviewReports;
    private long approvedReports;
    private long rejectedReports;
    private long validationFailedReports;

    public DomainReportSummaryResponse() {
    }

    public DomainReportSummaryResponse(
            UUID domainId,
            String domainName,
            String domainCode,
            LocalDate startDate,
            LocalDate endDate,
            long totalReports,
            long submittedReports,
            long underReviewReports,
            long approvedReports,
            long rejectedReports,
            long validationFailedReports
    ) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.domainCode = domainCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalReports = totalReports;
        this.submittedReports = submittedReports;
        this.underReviewReports = underReviewReports;
        this.approvedReports = approvedReports;
        this.rejectedReports = rejectedReports;
        this.validationFailedReports = validationFailedReports;
    }

    public UUID getDomainId() {
        return domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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