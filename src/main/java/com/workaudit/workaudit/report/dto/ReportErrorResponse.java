package com.workaudit.workaudit.report.dto;

import com.workaudit.workaudit.report.entity.ErrorType;

public class ReportErrorResponse {

    private ErrorType errorType;
    private String message;

    public ReportErrorResponse() {
    }

    public ReportErrorResponse(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public ErrorType getErrorType() { return errorType; }
    public void setErrorType(ErrorType errorType) { this.errorType = errorType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}