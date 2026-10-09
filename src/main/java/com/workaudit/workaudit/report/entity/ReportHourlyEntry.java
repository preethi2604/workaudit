package com.workaudit.workaudit.report.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "report_hourly_entries")
public class ReportHourlyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private DailyReport report;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "flagged_poor")
    private boolean flaggedPoor = false;

    protected ReportHourlyEntry() {
        // JPA
    }

    public ReportHourlyEntry(DailyReport report, LocalTime startTime, LocalTime endTime, String description) {
        this.report = report;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Long getId() { return id; }

    public DailyReport getReport() { return report; }
    public void setReport(DailyReport report) { this.report = report; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isFlaggedPoor() { return flaggedPoor; }
    public void setFlaggedPoor(boolean flaggedPoor) { this.flaggedPoor = flaggedPoor; }


    @Transient
    public double getDurationHours() {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            return 0.0;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;
    }
}