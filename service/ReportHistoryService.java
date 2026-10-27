package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.ReportHistoryResponse;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportHistory;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.ReportHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportHistoryService {

    private final ReportHistoryRepository reportHistoryRepository;
    private final DailyReportRepository dailyReportRepository;
    private final CurrentUserAuthorizationService authorizationService;

    public ReportHistoryService(
            ReportHistoryRepository reportHistoryRepository,
            DailyReportRepository dailyReportRepository,
            CurrentUserAuthorizationService authorizationService
    ) {
        this.reportHistoryRepository = reportHistoryRepository;
        this.dailyReportRepository = dailyReportRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * Records a report status transition.
     */
    public ReportHistory recordStatusChange(
            UUID reportId,
            ReportStatus previousStatus,
            ReportStatus newStatus,
            String reason,
            UUID changedBy
    ) {

        DailyReport report = dailyReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report not found: " + reportId
                        )
                );

        ReportHistory history = new ReportHistory();

        history.setReport(report);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        history.setChangedBy(changedBy);

        return reportHistoryRepository.save(history);
    }

    /**
     * Returns complete history for a report.
     */
    @Transactional(readOnly = true)
    public List<ReportHistoryResponse> getReportHistory(
            UUID reportId
    ) {

        DailyReport report = dailyReportRepository.findById(reportId).orElseThrow(() ->
                new IllegalArgumentException("Report not found: " + reportId));
        authorizationService.requireReportReadAccess(report);

        if (!dailyReportRepository.existsById(reportId)) {
            throw new IllegalArgumentException(
                    "Report not found: " + reportId
            );
        }

        return reportHistoryRepository
                .findByReportIdOrderByChangedAtDesc(reportId)
                .stream()
                .map(ReportHistoryResponse::fromEntity)
                .toList();
    }

    /**
     * Returns history entries created by a specific employee.
     */
    @Transactional(readOnly = true)
    public List<ReportHistoryResponse> getHistoryByUser(
            UUID changedBy
    ) {

        return reportHistoryRepository
                .findByChangedByOrderByChangedAtDesc(changedBy)
                .stream()
                .map(ReportHistoryResponse::fromEntity)
                .toList();
    }
}