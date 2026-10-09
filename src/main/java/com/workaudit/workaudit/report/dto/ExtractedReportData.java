package com.workaudit.workaudit.report.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExtractedReportData {

    private String employeeId;
    private String employeeName;
    private String department;
    private LocalDate reportDate;
    private String shiftTime;
    private String dailySummary;
    private String issuesFaced;
    private String nextDayPlan;
    private String selfEvaluation;
    private List<HourlyEntry> hourlyEntries = new ArrayList<>();
    private MeetingSummary meetingSummary;
    private String rawText;
    private boolean parseError = false;
    private String parseErrorMessage;

    public static class HourlyEntry {
        private final LocalTime slotStart;
        private final LocalTime slotEnd;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer declaredMinutes;
        private final String status;
        private final String description;

        public HourlyEntry(LocalTime slotStart, LocalTime slotEnd,
                           LocalTime startTime, LocalTime endTime,
                           Integer declaredMinutes, String status, String description) {
            this.slotStart = slotStart;
            this.slotEnd = slotEnd;
            this.startTime = startTime;
            this.endTime = endTime;
            this.declaredMinutes = declaredMinutes;
            this.status = status;
            this.description = description;
        }

        public LocalTime getSlotStart() { return slotStart; }
        public LocalTime getSlotEnd() { return slotEnd; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public Integer getDeclaredMinutes() { return declaredMinutes; }
        public String getStatus() { return status; }
        public String getDescription() { return description; }


        public boolean isBreak() {
            if (description == null) {
                return false;
            }
            String d = description.trim().toLowerCase();
            return d.length() <= 40
                    && (d.contains("lunch") || d.contains("tea break") || d.equals("break"));
        }
    }

    public static class MeetingSummary {
        private boolean present = false;
        private LocalTime meetingTime;
        private LocalTime meetingEndTime;
        private String discussion;

        public boolean isPresent() { return present; }
        public void setPresent(boolean present) { this.present = present; }

        public LocalTime getMeetingTime() { return meetingTime; }
        public void setMeetingTime(LocalTime meetingTime) { this.meetingTime = meetingTime; }

        public LocalTime getMeetingEndTime() { return meetingEndTime; }
        public void setMeetingEndTime(LocalTime meetingEndTime) { this.meetingEndTime = meetingEndTime; }

        public String getDiscussion() { return discussion; }
        public void setDiscussion(String discussion) { this.discussion = discussion; }
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public String getDailySummary() { return dailySummary; }
    public void setDailySummary(String dailySummary) { this.dailySummary = dailySummary; }

    public String getIssuesFaced() { return issuesFaced; }
    public void setIssuesFaced(String issuesFaced) { this.issuesFaced = issuesFaced; }

    public String getNextDayPlan() { return nextDayPlan; }
    public void setNextDayPlan(String nextDayPlan) { this.nextDayPlan = nextDayPlan; }

    public String getSelfEvaluation() { return selfEvaluation; }
    public void setSelfEvaluation(String selfEvaluation) { this.selfEvaluation = selfEvaluation; }

    public List<HourlyEntry> getHourlyEntries() { return hourlyEntries; }
    public void setHourlyEntries(List<HourlyEntry> hourlyEntries) { this.hourlyEntries = hourlyEntries; }

    public MeetingSummary getMeetingSummary() { return meetingSummary; }
    public void setMeetingSummary(MeetingSummary meetingSummary) { this.meetingSummary = meetingSummary; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public boolean isParseError() { return parseError; }
    public void setParseError(boolean parseError) { this.parseError = parseError; }

    public String getParseErrorMessage() { return parseErrorMessage; }
    public void setParseErrorMessage(String parseErrorMessage) { this.parseErrorMessage = parseErrorMessage; }
}