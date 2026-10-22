package com.workaudit.workaudit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class ReportRequest {

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotNull(message = "Domain ID is required")
    private UUID domainId;

    @NotNull(message = "Report date is required")
    private LocalDate reportDate;

    @NotBlank(message = "Daily summary is required")
    @Size(
            min = 20,
            max = 5000,
            message = "Daily summary must contain between 20 and 5000 characters"
    )
    private String dailySummary;

    @NotNull(message = "Working minutes are required")
    @Min(
            value = 1,
            message = "Working minutes must be greater than zero"
    )
    @Max(
            value = 1440,
            message = "Working minutes cannot exceed 24 hours"
    )
    private Integer totalWorkingMinutes;

    public ReportRequest() {
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getDomainId() {
        return domainId;
    }

    public void setDomainId(UUID domainId) {
        this.domainId = domainId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getDailySummary() {
        return dailySummary;
    }

    public void setDailySummary(String dailySummary) {
        this.dailySummary = dailySummary;
    }

    public Integer getTotalWorkingMinutes() {
        return totalWorkingMinutes;
    }

    public void setTotalWorkingMinutes(
            Integer totalWorkingMinutes
    ) {
        this.totalWorkingMinutes = totalWorkingMinutes;
    }
}