package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.ReviewReportRequest;
import com.workaudit.workaudit.dto.ReviewReportResponse;
import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ReportReviewService {

    private final DailyReportRepository dailyReportRepository;
    private final ReportHistoryService reportHistoryService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ReportReviewService(
            DailyReportRepository dailyReportRepository,
            ReportHistoryService reportHistoryService,
            NotificationService notificationService,
            AuditLogService auditLogService) {

        this.dailyReportRepository = dailyReportRepository;
        this.reportHistoryService = reportHistoryService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    /**
     * Moves a submitted report to manager review.
     */
    public ReviewReportResponse moveToReview(
            UUID reportId,
            UUID reviewedBy
    ) {

        DailyReport report = getReport(reportId);

        ReportStatus previousStatus =
                report.getStatus();

        report.moveToReview();

        DailyReport savedReport =
                dailyReportRepository.save(report);

        // Audit report review.
        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_REVIEWED,
                "REPORT",
                savedReport.getId(),
                "Report moved to manager review"
        );

        reportHistoryService.recordStatusChange(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                "Report moved to manager review",
                reviewedBy
        );

        return new ReviewReportResponse(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                "Report moved to manager review",
                reviewedBy,
                Instant.now()
        );
    }

    /**
     * Approves a report under review.
     */
    public ReviewReportResponse approveReport(
            UUID reportId,
            ReviewReportRequest request,
            UUID reviewedBy
    ) {

        DailyReport report = getReport(reportId);

        ReportStatus previousStatus =
                report.getStatus();

        report.approve();

        DailyReport savedReport =
                dailyReportRepository.save(report);

        // Audit report approval.
        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_APPROVED,
                "REPORT",
                savedReport.getId(),
                buildReasonDescription(
                        "Report approved",
                        request.getReason()
                )
        );

        reportHistoryService.recordStatusChange(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                request.getReason(),
                reviewedBy
        );

        notificationService.notifyReportApproved(
                savedReport,
                request.getReason()
        );

        return new ReviewReportResponse(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                request.getReason(),
                reviewedBy,
                Instant.now()
        );
    }

    /**
     * Rejects a report under review.
     */
    public ReviewReportResponse rejectReport(
            UUID reportId,
            ReviewReportRequest request,
            UUID reviewedBy
    ) {

        DailyReport report = getReport(reportId);

        ReportStatus previousStatus =
                report.getStatus();

        report.reject();

        DailyReport savedReport =
                dailyReportRepository.save(report);

        // Audit report rejection.
        auditLogService.record(
                getCurrentUserEmail(),
                AuditAction.REPORT_REJECTED,
                "REPORT",
                savedReport.getId(),
                buildReasonDescription(
                        "Report rejected",
                        request.getReason()
                )
        );

        reportHistoryService.recordStatusChange(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                request.getReason(),
                reviewedBy
        );

        notificationService.notifyReportRejected(
                savedReport,
                request.getReason()
        );

        return new ReviewReportResponse(
                savedReport.getId(),
                previousStatus,
                savedReport.getStatus(),
                request.getReason(),
                reviewedBy,
                Instant.now()
        );
    }

    /**
     * Get the email of the currently authenticated user.
     *
     * The email comes from the JWT/Spring Security context.
     */
    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        return authentication.getName();
    }

    /**
     * Build a clean audit description with an optional reason.
     */
    private String buildReasonDescription(
            String action,
            String reason) {

        if (reason == null || reason.isBlank()) {
            return action;
        }

        return action + ": " + reason;
    }

    private DailyReport getReport(UUID reportId) {

        return dailyReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report not found: " + reportId
                        )
                );
    }
}