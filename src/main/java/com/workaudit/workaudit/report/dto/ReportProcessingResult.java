package com.workaudit.workaudit.report.dto;

import com.workaudit.workaudit.report.entity.DescriptionLevel;
import com.workaudit.workaudit.report.entity.ReportStatus;

import java.util.ArrayList;
import java.util.List;

public class ReportProcessingResult {

    private Long reportId;
    private String employeeId;
    private ReportStatus status;
    private double totalWorkingHours;
    private boolean hoursOk;
    private boolean dateOk;
    private boolean lateSubmission;
    private DescriptionLevel descriptionLevel;
    private boolean meetingOk;
    private boolean duplicateFlagged;
    private boolean parseError;
    private String parseErrorMessage;
    private List<ReportErrorResponse> errors = new ArrayList<>();

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public double getTotalWorkingHours() { return totalWorkingHours; }
    public void setTotalWorkingHours(double totalWorkingHours) { this.totalWorkingHours = totalWorkingHours; }

    public boolean isHoursOk() { return hoursOk; }
    public void setHoursOk(boolean hoursOk) { this.hoursOk = hoursOk; }

    public boolean isDateOk() { return dateOk; }
    public void setDateOk(boolean dateOk) { this.dateOk = dateOk; }

    public boolean isLateSubmission() { return lateSubmission; }
    public void setLateSubmission(boolean lateSubmission) { this.lateSubmission = lateSubmission; }

    public DescriptionLevel getDescriptionLevel() { return descriptionLevel; }
    public void setDescriptionLevel(DescriptionLevel descriptionLevel) { this.descriptionLevel = descriptionLevel; }

    public boolean isMeetingOk() { return meetingOk; }
    public void setMeetingOk(boolean meetingOk) { this.meetingOk = meetingOk; }

    public boolean isDuplicateFlagged() { return duplicateFlagged; }
    public void setDuplicateFlagged(boolean duplicateFlagged) { this.duplicateFlagged = duplicateFlagged; }

    public boolean isParseError() { return parseError; }
    public void setParseError(boolean parseError) { this.parseError = parseError; }

    public String getParseErrorMessage() { return parseErrorMessage; }
    public void setParseErrorMessage(String parseErrorMessage) { this.parseErrorMessage = parseErrorMessage; }

    public List<ReportErrorResponse> getErrors() { return errors; }
    public void setErrors(List<ReportErrorResponse> errors) { this.errors = errors; }
}