package com.workaudit.workaudit.report.entity;

public enum ErrorType {
    BELOW_REQUIRED_HOURS("Working hours below required threshold"),
    HOURLY_DESCRIPTION_MISSING("Hourly description missing"),
    MEETING_SUMMARY_MISSING("Manager meeting summary missing"),
    REPEATED_DESCRIPTION("Repeated description detected"),
    WRONG_REPORT_DATE("Report date does not match expected working date"),
    LATE_SUBMISSION("Report submitted late"),
    POOR_DESCRIPTION("Description too short or generic"),
    DUPLICATE_REPORT("Highly similar to another employee's report"),
    MISSING_REPORT("Report not submitted"),
    DAILY_SUMMARY_MISSING("Daily summary missing"),
    INVALID_FORMAT("PDF/report format invalid or unreadable"),
    EMPLOYEE_DOMAIN_MISMATCH("Employee/domain information incorrect or missing"),
    LATE_LOGIN("Logged in after office start time");

    private final String defaultMessage;

    ErrorType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}