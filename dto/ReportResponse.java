package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ReportResponse {

    private UUID id;

    private UUID employeeId;
    private String employeeName;

    private UUID domainId;
    private String domainName;

    private LocalDate reportDate;

    private Instant submittedAt;

    private String dailySummary;

    private Integer totalWorkingMinutes;

    private Double totalWorkingHours;

    private ReportStatus status;

    private boolean attachmentPresent;
    private String attachmentFileName;
    private String attachmentContentType;
    private Long attachmentSize;

    private Instant createdAt;
    private Instant updatedAt;

    public ReportResponse() {
    }

    public static ReportResponse fromEntity(
            DailyReport report
    ) {

        ReportResponse response = new ReportResponse();

        response.id = report.getId();

        response.employeeId =
                report.getEmployee().getId();

        response.employeeName =
                report.getEmployee().getFirstName()
                        + " "
                        + report.getEmployee().getLastName();

        response.domainId =
                report.getDomain().getId();

        response.domainName =
                report.getDomain().getName();

        response.reportDate =
                report.getReportDate();

        response.submittedAt =
                report.getSubmittedAt();

        response.dailySummary =
                report.getDailySummary();

        response.totalWorkingMinutes =
                report.getTotalWorkingMinutes();

        response.totalWorkingHours =
                report.getTotalWorkingMinutes() / 60.0;

        response.status =
                report.getStatus();

        response.attachmentPresent = report.hasAttachment();
        response.attachmentFileName = report.getAttachmentFileName();
        response.attachmentContentType = report.getAttachmentContentType();
        response.attachmentSize = report.getAttachmentSize();

        response.createdAt =
                report.getCreatedAt();

        response.updatedAt =
                report.getUpdatedAt();

        return response;
    }

    public UUID getId() {
        return id;
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

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public String getDailySummary() {
        return dailySummary;
    }

    public Integer getTotalWorkingMinutes() {
        return totalWorkingMinutes;
    }

    public Double getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public boolean isAttachmentPresent() {
        return attachmentPresent;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public Long getAttachmentSize() {
        return attachmentSize;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}