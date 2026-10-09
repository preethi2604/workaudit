package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "report_date"}))
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;

    @Column(name = "domain_id")
    private Long domainId;

    @Column(name = "domain_name", length = 100)
    private String domainName;

    @Column(name = "project_id")
    private Long projectId;

    // The date this report is FOR (extracted from the PDF content)
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    // The working date this upload was expected against
    @Column(name = "expected_date", nullable = false)
    private LocalDate expectedDate;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "daily_summary", columnDefinition = "TEXT")
    private String dailySummary;

    @Column(name = "total_working_hours")
    private Double totalWorkingHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReportStatus status = ReportStatus.SUBMITTED;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportHourlyEntry> hourlyEntries = new ArrayList<>();

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ManagerMeeting managerMeeting;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected DailyReport() {
        // JPA
    }

    public DailyReport(String employeeId, LocalDate expectedDate) {
        this.employeeId = employeeId;
        this.expectedDate = expectedDate;
        this.submittedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Long getDomainId() { return domainId; }
    public void setDomainId(Long domainId) { this.domainId = domainId; }

    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public String getDailySummary() { return dailySummary; }
    public void setDailySummary(String dailySummary) { this.dailySummary = dailySummary; }

    public Double getTotalWorkingHours() { return totalWorkingHours; }
    public void setTotalWorkingHours(Double totalWorkingHours) { this.totalWorkingHours = totalWorkingHours; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public List<ReportHourlyEntry> getHourlyEntries() { return hourlyEntries; }
    public void setHourlyEntries(List<ReportHourlyEntry> hourlyEntries) { this.hourlyEntries = hourlyEntries; }

    public ManagerMeeting getManagerMeeting() { return managerMeeting; }
    public void setManagerMeeting(ManagerMeeting managerMeeting) { this.managerMeeting = managerMeeting; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}