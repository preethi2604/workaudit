package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.HistoricalReportResponse;
import com.workaudit.workaudit.dto.ReportTrendResponse;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HistoricalReportService {

    private final DailyReportRepository dailyReportRepository;

    public HistoricalReportService(
            DailyReportRepository dailyReportRepository
    ) {
        this.dailyReportRepository = dailyReportRepository;
    }

    /**
     * Returns all reports between two dates.
     */
    public List<HistoricalReportResponse> getHistory(
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        return dailyReportRepository
                .findByReportDateBetweenOrderByReportDateAsc(
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns historical reports for one employee.
     */
    public List<HistoricalReportResponse> getEmployeeHistory(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        return dailyReportRepository
                .findByEmployeeIdAndReportDateBetweenOrderByReportDateAsc(
                        employeeId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns historical reports for one domain.
     */
    public List<HistoricalReportResponse> getDomainHistory(
            UUID domainId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        return dailyReportRepository
                .findByDomainIdAndReportDateBetweenOrderByReportDateAsc(
                        domainId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates daily trend information.
     */
    public List<ReportTrendResponse> getDailyTrends(
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        List<DailyReport> reports =
                dailyReportRepository
                        .findByReportDateBetweenOrderByReportDateAsc(
                                startDate,
                                endDate
                        );

        // Group once instead of filtering the complete report list for every day.
        // This keeps chart generation efficient for long historical date ranges.
        Map<LocalDate, List<DailyReport>> reportsByDate = reports.stream()
                .collect(Collectors.groupingBy(DailyReport::getReportDate));

        List<ReportTrendResponse> trends = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            trends.add(buildTrend(
                    currentDate,
                    reportsByDate.getOrDefault(currentDate, List.of())
            ));
            currentDate = currentDate.plusDays(1);
        }

        return trends;
    }

    private ReportTrendResponse buildTrend(
            LocalDate date,
            List<DailyReport> reports
    ) {

        return new ReportTrendResponse(
                date,
                reports.size(),
                countStatus(
                        reports,
                        ReportStatus.SUBMITTED
                ),
                countStatus(
                        reports,
                        ReportStatus.UNDER_REVIEW
                ),
                countStatus(
                        reports,
                        ReportStatus.APPROVED
                ),
                countStatus(
                        reports,
                        ReportStatus.REJECTED
                ),
                countStatus(
                        reports,
                        ReportStatus.VALIDATION_FAILED
                )
        );
    }

    private long countStatus(
            List<DailyReport> reports,
            ReportStatus status
    ) {

        return reports.stream()
                .filter(report ->
                        report.getStatus() == status
                )
                .count();
    }

    private HistoricalReportResponse toResponse(
            DailyReport report
    ) {

        String employeeName =
                report.getEmployee().getFirstName()
                        + " "
                        + report.getEmployee().getLastName();

        return new HistoricalReportResponse(
                report.getId(),
                report.getEmployee().getId(),
                employeeName.trim(),
                report.getDomain().getId(),
                report.getDomain().getName(),
                report.getReportDate(),
                report.getStatus(),
                report.getTotalWorkingMinutes()
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