package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "report_errors")
public class ReportError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private DailyReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false, length = 50)
    private ErrorType errorType;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ReportError() {
        // JPA
    }

    public ReportError(DailyReport report, ErrorType errorType, String message) {
        this.report = report;
        this.errorType = errorType;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public DailyReport getReport() { return report; }

    public ErrorType getErrorType() { return errorType; }

    public String getMessage() { return message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}