package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.ValidationResult;
import com.workaudit.workaudit.entity.ValidationStatus;

import java.time.Instant;
import java.util.UUID;

public class ValidationResponse {

    private UUID id;

    private UUID reportId;

    private ValidationStatus overallStatus;

    private Double workingHours;

    private boolean descriptionValid;

    private boolean late;

    private boolean wrongDate;

    private boolean duplicateSuspected;

    private boolean repeatedContent;

    private Instant validatedAt;

    public ValidationResponse() {
    }

    public static ValidationResponse fromEntity(
            ValidationResult result
    ) {

        ValidationResponse response =
                new ValidationResponse();

        response.id = result.getId();

        response.reportId =
                result.getReport().getId();

        response.overallStatus =
                result.getOverallStatus();

        response.workingHours =
                result.getWorkingHours();

        response.descriptionValid =
                result.isDescriptionValid();

        response.late =
                result.isLate();

        response.wrongDate =
                result.isWrongDate();

        response.duplicateSuspected =
                result.isDuplicateSuspected();

        response.repeatedContent =
                result.isRepeatedContent();

        response.validatedAt =
                result.getValidatedAt();

        return response;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReportId() {
        return reportId;
    }

    public ValidationStatus getOverallStatus() {
        return overallStatus;
    }

    public Double getWorkingHours() {
        return workingHours;
    }

    public boolean isDescriptionValid() {
        return descriptionValid;
    }

    public boolean isLate() {
        return late;
    }

    public boolean isWrongDate() {
        return wrongDate;
    }

    public boolean isDuplicateSuspected() {
        return duplicateSuspected;
    }

    public boolean isRepeatedContent() {
        return repeatedContent;
    }

    public Instant getValidatedAt() {
        return validatedAt;
    }
}