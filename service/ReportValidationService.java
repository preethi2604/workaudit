package com.workaudit.workaudit.service;

import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportError;
import com.workaudit.workaudit.entity.ReportErrorType;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.entity.ValidationResult;
import com.workaudit.workaudit.entity.ValidationStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.ReportErrorRepository;
import com.workaudit.workaudit.repository.ValidationResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportValidationService {

    private final DailyReportRepository reportRepository;
    private final ValidationResultRepository validationRepository;
    private final ReportErrorRepository errorRepository;

    private final double requiredWorkingHours;
    private final double halfDayHours;
    private final int lateSubmissionHour;
    private final int poorDescriptionMinLength;
    private final NotificationService notificationService;
    private final ReportHistoryService reportHistoryService;
    private final AuditLogService auditLogService;
    private final CurrentUserAuthorizationService authorizationService;

    public ReportValidationService(
            DailyReportRepository reportRepository,
            ValidationResultRepository validationRepository,
            ReportErrorRepository errorRepository,
            @Value("${workaudit.validation.required-working-hours:9}")
            double requiredWorkingHours,
            @Value("${workaudit.validation.half-day-hours:4.5}")
            double halfDayHours,
            @Value("${workaudit.validation.late-submission-hour:23}")
            int lateSubmissionHour,
            @Value("${workaudit.validation.poor-description-min-length:20}")
            int poorDescriptionMinLength,
            NotificationService notificationService,
            ReportHistoryService reportHistoryService,
            AuditLogService auditLogService,
            CurrentUserAuthorizationService authorizationService
    ) {
        this.reportRepository = reportRepository;
        this.validationRepository = validationRepository;
        this.errorRepository = errorRepository;
        this.requiredWorkingHours = requiredWorkingHours;
        this.halfDayHours = halfDayHours;
        this.lateSubmissionHour = lateSubmissionHour;
        this.poorDescriptionMinLength = poorDescriptionMinLength;
        this.notificationService = notificationService;
        this.reportHistoryService = reportHistoryService;
        this.auditLogService = auditLogService;
        this.authorizationService = authorizationService;
    }

    public ValidationResult validateReport(
            UUID reportId
    ) {

        DailyReport report =
                reportRepository.findById(reportId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Report not found: " + reportId
                                )
                        );

        authorizationService.requireReportReadAccess(report);
        clearPreviousValidation(reportId);

        List<ReportError> errors =
                new ArrayList<>();

        double workingHours =
                report.getTotalWorkingMinutes() / 60.0;

        boolean descriptionValid =
                report.getDailySummary() != null
                        && report.getDailySummary().trim().length()
                        >= poorDescriptionMinLength;

        boolean late =
                isLate(report);

        boolean wrongDate =
                isWrongDate(report);

        if (workingHours < requiredWorkingHours) {

            errors.add(
                    new ReportError(
                            report,
                            ReportErrorType.BELOW_REQUIRED_HOURS,
                            String.format(
                                    "Working hours %.2f are below required %.2f hours",
                                    workingHours,
                                    requiredWorkingHours
                            )
                    )
            );
        }

        if (workingHours < halfDayHours) {

            errors.add(
                    new ReportError(
                            report,
                            ReportErrorType.BELOW_REQUIRED_HOURS,
                            String.format(
                                    "Working hours %.2f are below half-day threshold %.2f hours",
                                    workingHours,
                                    halfDayHours
                            )
                    )
            );
        }

        if (!descriptionValid) {

            errors.add(
                    new ReportError(
                            report,
                            ReportErrorType.POOR_DESCRIPTION,
                            "Daily report description is too short"
                    )
            );
        }

        if (late) {

            errors.add(
                    new ReportError(
                            report,
                            ReportErrorType.LATE_SUBMISSION,
                            "Report was submitted after the allowed submission time"
                    )
            );
        }

        if (wrongDate) {

            errors.add(
                    new ReportError(
                            report,
                            ReportErrorType.WRONG_DATE,
                            "Report date does not match the current reporting date"
                    )
            );
        }

        if (!errors.isEmpty()) {
            errorRepository.saveAll(errors);
        }

        ValidationStatus status =
                determineStatus(
                        workingHours,
                        descriptionValid,
                        late,
                        wrongDate
                );

        ValidationResult result =
                new ValidationResult(
                        report,
                        status,
                        workingHours,
                        descriptionValid,
                        late,
                        wrongDate,
                        false,
                        false
                );

        ValidationResult saved =
                validationRepository.save(result);

        ReportStatus previousStatus = report.getStatus();

        if (status == ValidationStatus.FAIL) {
            report.setStatus(ReportStatus.VALIDATION_FAILED);
            reportRepository.save(report);

            reportHistoryService.recordStatusChange(
                    report.getId(),
                    previousStatus,
                    report.getStatus(),
                    "Automatic validation failed",
                    null
            );

            notificationService.notifyValidationFailed(report);
        }

        AuditAction auditAction = switch (status) {
            case PASS -> AuditAction.REPORT_VALIDATION_PASSED;
            case WARNING -> AuditAction.REPORT_VALIDATION_WARNING;
            case FAIL -> AuditAction.REPORT_VALIDATION_FAILED;
        };

        auditLogService.record(
                getCurrentUserEmail(),
                auditAction,
                "REPORT",
                report.getId(),
                "Report validation result: " + status
                        + "; workingHours=" + String.format("%.2f", workingHours)
                        + "; errorCount=" + errors.size()
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public ValidationResult getValidation(
            UUID reportId
    ) {

        DailyReport report = reportRepository.findById(reportId).orElseThrow(() ->
                new IllegalArgumentException("Report not found: " + reportId));
        authorizationService.requireReportReadAccess(report);
        return validationRepository
                .findByReportId(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Validation result not found for report: "
                                        + reportId
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<ReportError> getErrors(
            UUID reportId
    ) {

        DailyReport report = reportRepository.findById(reportId).orElseThrow(() ->
                new IllegalArgumentException("Report not found: " + reportId));
        authorizationService.requireReportReadAccess(report);
        return errorRepository
                .findByReportIdOrderByCreatedAtAsc(
                        reportId
                );
    }

    private ValidationStatus determineStatus(
            double workingHours,
            boolean descriptionValid,
            boolean late,
            boolean wrongDate
    ) {

        if (wrongDate) {
            return ValidationStatus.FAIL;
        }

        if (workingHours < halfDayHours) {
            return ValidationStatus.FAIL;
        }

        if (!descriptionValid) {
            return ValidationStatus.WARNING;
        }

        if (workingHours < requiredWorkingHours) {
            return ValidationStatus.WARNING;
        }

        if (late) {
            return ValidationStatus.WARNING;
        }

        return ValidationStatus.PASS;
    }

    private boolean isLate(
            DailyReport report
    ) {

        if (report.getSubmittedAt() == null) {
            return false;
        }

        LocalTime submissionTime =
                report.getSubmittedAt()
                        .atZone(
                                java.time.ZoneOffset.UTC
                        )
                        .toLocalTime();

        return submissionTime.getHour()
                >= lateSubmissionHour;
    }

    private boolean isWrongDate(
            DailyReport report
    ) {

        if (report.getSubmittedAt() == null) {
            return false;
        }

        LocalDate submissionDate =
                report.getSubmittedAt()
                        .atZone(
                                java.time.ZoneOffset.UTC
                        )
                        .toLocalDate();

        return !submissionDate.equals(
                report.getReportDate()
        );
    }

    private String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return authentication.getName();
    }

    private void clearPreviousValidation(
            UUID reportId
    ) {

        List<ReportError> oldErrors =
                errorRepository
                        .findByReportIdOrderByCreatedAtAsc(
                                reportId
                        );

        if (!oldErrors.isEmpty()) {
            errorRepository.deleteAll(oldErrors);
        }

        validationRepository
                .findByReportId(reportId)
                .ifPresent(validationRepository::delete);
    }
}