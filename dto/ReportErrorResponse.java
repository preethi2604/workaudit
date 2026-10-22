package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.ReportError;
import com.workaudit.workaudit.entity.ReportErrorType;

import java.time.Instant;
import java.util.UUID;

public class ReportErrorResponse {

    private UUID id;

    private UUID reportId;

    private ReportErrorType errorType;

    private String message;

    private Instant createdAt;

    public ReportErrorResponse() {
    }

    public static ReportErrorResponse fromEntity(
            ReportError error
    ) {

        ReportErrorResponse response =
                new ReportErrorResponse();

        response.id = error.getId();

        response.reportId =
                error.getReport().getId();

        response.errorType =
                error.getErrorType();

        response.message =
                error.getMessage();

        response.createdAt =
                error.getCreatedAt();

        return response;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReportId() {
        return reportId;
    }

    public ReportErrorType getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}