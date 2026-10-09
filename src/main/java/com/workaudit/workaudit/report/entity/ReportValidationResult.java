package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_validation_results")
public class ReportValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private DailyReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "description_level", nullable = false, length = 20)
    private DescriptionLevel descriptionLevel;

    @Column(name = "hours_ok", nullable = false)
    private boolean hoursOk;

    @Column(name = "date_ok", nullable = false)
    private boolean dateOk;

    @Column(name = "meeting_ok", nullable = false)
    private boolean meetingOk;

    @Column(name = "duplicate_flagged", nullable = false)
    private boolean duplicateFlagged;

    @Column(name = "overall_pass", nullable = false)
    private boolean overallPass;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    protected ReportValidationResult() {
        // JPA
    }

    public ReportValidationResult(DailyReport report) {
        this.report = report;
        this.computedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public DailyReport getReport() { return report; }

    public DescriptionLevel getDescriptionLevel() { return descriptionLevel; }
    public void setDescriptionLevel(DescriptionLevel descriptionLevel) { this.descriptionLevel = descriptionLevel; }

    public boolean isHoursOk() { return hoursOk; }
    public void setHoursOk(boolean hoursOk) { this.hoursOk = hoursOk; }

    public boolean isDateOk() { return dateOk; }
    public void setDateOk(boolean dateOk) { this.dateOk = dateOk; }

    public boolean isMeetingOk() { return meetingOk; }
    public void setMeetingOk(boolean meetingOk) { this.meetingOk = meetingOk; }

    public boolean isDuplicateFlagged() { return duplicateFlagged; }
    public void setDuplicateFlagged(boolean duplicateFlagged) { this.duplicateFlagged = duplicateFlagged; }

    public boolean isOverallPass() { return overallPass; }
    public void setOverallPass(boolean overallPass) { this.overallPass = overallPass; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}