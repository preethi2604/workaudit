package com.workaudit.workaudit.dto;

import java.util.UUID;

public class DomainDashboardResponse {

    private UUID domainId;
    private String domainName;
    private String domainCode;

    private long totalReports;
    private long submittedReports;
    private long underReviewReports;
    private long approvedReports;
    private long rejectedReports;
    private long validationFailedReports;

    public DomainDashboardResponse() {
    }

    public DomainDashboardResponse(
            UUID domainId,
            String domainName,
            String domainCode,
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