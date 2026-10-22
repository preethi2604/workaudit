package com.workaudit.workaudit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "validation_results")
public class ValidationResult {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "report_id",
            nullable = false,
            unique = true
    )
    private DailyReport report;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "overall_status",
            nullable = false,
            length = 20
    )
    private ValidationStatus overallStatus;

    @Column(name = "working_hours", nullable = false)
    private Double workingHours;

    @Column(name = "description_valid", nullable = false)
    private boolean descriptionValid;

    @Column(name = "late", nullable = false)
    private boolean late;

    @Column(name = "wrong_date", nullable = false)
    private boolean wrongDate;

    @Column(name = "duplicate_suspected", nullable = false)
    private boolean duplicateSuspected;

    @Column(name = "repeated_content", nullable = false)
    private boolean repeatedContent;

    @Column(name = "validated_at", nullable = false)
    private Instant validatedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ValidationResult() {
    }

    public ValidationResult(
            DailyReport report,
            ValidationStatus overallStatus,
            Double workingHours,
            boolean descriptionValid,
            boolean late,
            boolean wrongDate,
            boolean duplicateSuspected,
            boolean repeatedContent
    ) {
        this.id = UUID.randomUUID();
        this.report = report;
        this.overallStatus = overallStatus;
        this.workingHours = workingHours;
        this.descriptionValid = descriptionValid;
        this.late = late;
        this.wrongDate = wrongDate;
        this.duplicateSuspected = duplicateSuspected;
        this.repeatedContent = repeatedContent;
    }

    @PrePersist
    protected void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        Instant now = Instant.now();

        if (validatedAt == null) {
            validatedAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public DailyReport getReport() {
        return report;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setOverallStatus(
            ValidationStatus overallStatus
    ) {
        this.overallStatus = overallStatus;
    }
}