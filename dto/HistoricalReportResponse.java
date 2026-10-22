package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.ReportStatus;

import java.time.LocalDate;
import java.util.UUID;

public class HistoricalReportResponse {

    private UUID reportId;
    private UUID employeeId;
    private String employeeName;

    private UUID domainId;
    private String domainName;

    private LocalDate reportDate;
    private ReportStatus status;

    private Integer totalWorkingMinutes;

    public HistoricalReportResponse() {
    }

    public HistoricalReportResponse(
            UUID reportId,
            UUID employeeId,
            String employeeName,
            UUID domainId,
            String domainName,
            LocalDate reportDate,
            ReportStatus status,
            Integer totalWorkingMinutes
    ) {
        this.reportId = reportId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.domainId = domainId;
        this.domainName = domainName;
        this.reportDate = reportDate;
        this.status = status;
        this.totalWorkingMinutes = totalWorkingMinutes;
    }

    public UUID getReportId() {
        return reportId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public UUID getDomainId() {
        return domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Integer getTotalWorkingMinutes() {
        return totalWorkingMinutes;
    }
}