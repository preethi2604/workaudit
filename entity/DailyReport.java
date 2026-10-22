package com.workaudit.workaudit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "daily_reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_report_employee_date",
                        columnNames = {
                                "employee_id",
                                "report_date"
                        }
                )
        }
)
public class DailyReport {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false
    )
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "domain_id",
            nullable = false
    )
    private Domain domain;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "daily_summary", nullable = false, length = 5000)
    private String dailySummary;

    @Column(name = "total_working_minutes", nullable = false)
    private Integer totalWorkingMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;

    @Column(name = "attachment_file_name", length = 255)
    private String attachmentFileName;

    @Column(name = "attachment_content_type", length = 100)
    private String attachmentContentType;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "attachment_data", columnDefinition = "bytea")
    private byte[] attachmentData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DailyReport() {
    }

    public DailyReport(
            Employee employee,
            Domain domain,
            LocalDate reportDate,
            String dailySummary,
            Integer totalWorkingMinutes
    ) {
        this.id = UUID.randomUUID();
        this.employee = employee;
        this.domain = domain;
        this.reportDate = reportDate;
        this.dailySummary = dailySummary;
        this.totalWorkingMinutes = totalWorkingMinutes;
        this.status = ReportStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void revise(Domain domain, LocalDate reportDate,
                       String dailySummary, Integer totalWorkingMinutes) {
        if (status != ReportStatus.DRAFT && status != ReportStatus.VALIDATION_FAILED) {
            throw new IllegalStateException("Only draft or validation-failed reports can be edited");
        }
        this.domain = domain;
        this.reportDate = reportDate;
        this.dailySummary = dailySummary;
        this.totalWorkingMinutes = totalWorkingMinutes;
        if (status == ReportStatus.VALIDATION_FAILED) {
            this.status = ReportStatus.DRAFT;
            this.submittedAt = null;
        }
    }

    public void submit() {

        if (status != ReportStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft reports can be submitted"
            );
        }

        this.status = ReportStatus.SUBMITTED;
        submittedAt = Instant.now();
    }

    public void moveToReview() {

        if (status != ReportStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only submitted reports can move to review"
            );
        }

        this.status = ReportStatus.UNDER_REVIEW;
    }

    public void approve() {

        if (status != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only reports under review can be approved"
            );
        }

        this.status = ReportStatus.APPROVED;
    }

    public void reject() {

        if (status != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Only reports under review can be rejected"
            );
        }

        this.status = ReportStatus.REJECTED;
    }

    public UUID getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Domain getDomain() {
        return domain;
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

    public ReportStatus getStatus() {
        return status;
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

    public byte[] getAttachmentData() {
        return attachmentData;
    }

    public boolean hasAttachment() {
        return attachmentData != null && attachmentData.length > 0;
    }

    public void setAttachment(
            String fileName,
            String contentType,
            byte[] data) {
        this.attachmentFileName = fileName;
        this.attachmentContentType = contentType;
        this.attachmentData = data;
        this.attachmentSize = data == null ? null : (long) data.length;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setDailySummary(String dailySummary) {
        this.dailySummary = dailySummary;
    }

    public void setTotalWorkingMinutes(
            Integer totalWorkingMinutes
    ) {
        this.totalWorkingMinutes = totalWorkingMinutes;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}