package com.workaudit.workaudit.dto;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeReportSummaryResponse {

    private UUID employeeId;
    private String employeeName;

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalReports;
    private long submittedReports;
    private long underReviewReports;
    private long approvedReports;
    private long rejectedReports;
    private long validationFailedReports;

    public EmployeeReportSummaryResponse() {
    }

    public EmployeeReportSummaryResponse(
            UUID employeeId,
            String employeeName,
            LocalDate startDate,
            LocalDate endDate,
            long totalReports,
            long submittedReports,
            long underReviewReports,
            long approvedReports,
            long rejectedReports,
            long validationFailedReports
    ) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalReports = totalReports;
        this.submittedReports = submittedReports;
        this.underReviewReports = underReviewReports;
        this.approvedReports = approvedReports;
        this.rejectedReports = rejectedReports;
        this.validationFailedReports = validationFailedReports;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
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