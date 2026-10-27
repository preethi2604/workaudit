package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.DomainReportSummaryResponse;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.Domain;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.DomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DomainReportService {

    private final DomainRepository domainRepository;
    private final DailyReportRepository dailyReportRepository;

    public DomainReportService(
            DomainRepository domainRepository,
            DailyReportRepository dailyReportRepository
    ) {
        this.domainRepository = domainRepository;
        this.dailyReportRepository = dailyReportRepository;
    }

    public List<ReportResponse> getDomainReports(
            UUID domainId,
            LocalDate reportDate
    ) {

        getDomain(domainId);

        if (reportDate == null) {
            throw new IllegalArgumentException(
                    "Report date is required"
            );
        }

        return dailyReportRepository
                .findByDomainIdAndReportDate(
                        domainId,
                        reportDate
                )
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    public DomainReportSummaryResponse getDomainSummary(
            UUID domainId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        Domain domain = getDomain(domainId);

        validateDateRange(startDate, endDate);

        List<DailyReport> reports =
                dailyReportRepository.findAll()
                        .stream()
                        .filter(report ->
                                report.getDomain()
                                        .getId()
                                        .equals(domainId)
                        )
                        .filter(report ->
                                !report.getReportDate()
                                        .isBefore(startDate)
                        )
                        .filter(report ->
                                !report.getReportDate()
                                        .isAfter(endDate)
                        )
                        .toList();

        long submitted = countByStatus(
                reports,
                ReportStatus.SUBMITTED
        );

        long underReview = countByStatus(
                reports,
                ReportStatus.UNDER_REVIEW
        );

        long approved = countByStatus(
                reports,
                ReportStatus.APPROVED
        );

        long rejected = countByStatus(
                reports,
                ReportStatus.REJECTED
        );

        long validationFailed = countByStatus(
                reports,
                ReportStatus.VALIDATION_FAILED
        );

        return new DomainReportSummaryResponse(
                domain.getId(),
                domain.getName(),
                domain.getCode(),
                startDate,
                endDate,
                reports.size(),
                submitted,
                underReview,
                approved,
                rejected,
                validationFailed
        );
    }

    private long countByStatus(
            List<DailyReport> reports,
            ReportStatus status
    ) {

        return reports.stream()
                .filter(report -> report.getStatus() == status)
                .count();
    }

    private Domain getDomain(UUID domainId) {

        return domainRepository.findById(domainId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: " + domainId
                        )
                );
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }
    }
}