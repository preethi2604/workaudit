package com.workaudit.workaudit.report.dto;

import com.workaudit.workaudit.report.entity.ReportStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate reportDate;
    private Double hoursWorked;
    private ReportStatus status;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReportResponse() {
    }

    public ReportResponse(
            Long id,
            String title,
            String description,
            LocalDate reportDate,
            Double hoursWorked,
            ReportStatus status,
            Long employeeId,
            String employeeCode,
            String employeeName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.reportDate = reportDate;
        this.hoursWorked = hoursWorked;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}