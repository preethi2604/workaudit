package com.workaudit.workaudit.report.dto;

import com.workaudit.workaudit.report.entity.DescriptionLevel;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationResultResponse {

    private Long reportId;
    private DescriptionLevel descriptionLevel;
    private boolean hoursOk;
    private boolean dateOk;
    private boolean meetingOk;
    private boolean duplicateFlagged;
    private boolean overallPass;
    private LocalDateTime computedAt;
    private List<ReportErrorResponse> errors;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

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

    public List<ReportErrorResponse> getErrors() { return errors; }
    public void setErrors(List<ReportErrorResponse> errors) { this.errors = errors; }
}