package com.workaudit.workaudit.dto;

public class DashboardResponse {

    private long totalEmployees;
    private long activeEmployees;

    private long totalDomains;
    private long activeDomains;

    private long totalReports;
    private long submittedReports;
    private long underReviewReports;
    private long approvedReports;
    private long rejectedReports;
    private long validationFailedReports;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalEmployees,
            long activeEmployees,
            long totalDomains,
            long activeDomains,
            long totalReports,
            long submittedReports,
            long underReviewReports,
            long approvedReports,
            long rejectedReports,
            long validationFailedReports
    ) {
        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.totalDomains = totalDomains;
        this.activeDomains = activeDomains;
        this.totalReports = totalReports;
        this.submittedReports = submittedReports;
        this.underReviewReports = underReviewReports;
        this.approvedReports = approvedReports;
        this.rejectedReports = rejectedReports;
        this.validationFailedReports = validationFailedReports;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public long getTotalDomains() {
        return totalDomains;
    }

    public long getActiveDomains() {
        return activeDomains;
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