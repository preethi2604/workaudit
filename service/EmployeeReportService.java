package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.EmployeeReportSummaryResponse;
import com.workaudit.workaudit.dto.ReportResponse;
import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmployeeReportService {

    private final EmployeeRepository employeeRepository;
    private final DailyReportRepository dailyReportRepository;
    private final CurrentUserAuthorizationService authorizationService;

    public EmployeeReportService(
            EmployeeRepository employeeRepository,
            DailyReportRepository dailyReportRepository,
            CurrentUserAuthorizationService authorizationService
    ) {
        this.employeeRepository = employeeRepository;
        this.dailyReportRepository = dailyReportRepository;
        this.authorizationService = authorizationService;
    }

    public List<ReportResponse> getEmployeeReports(
            UUID employeeId
    ) {

        authorizationService.requireEmployeeScope(employeeId);
        Employee employee = getEmployee(employeeId);

        return dailyReportRepository
                .findByEmployeeIdOrderByReportDateDesc(employee.getId())
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    public List<ReportResponse> getEmployeeReportsByDateRange(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        authorizationService.requireEmployeeScope(employeeId);
        getEmployee(employeeId);

        validateDateRange(startDate, endDate);

        return dailyReportRepository
                .findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
                        employeeId,
                        startDate,
                        endDate
                )
                .stream()
                .map(ReportResponse::fromEntity)
                .toList();
    }

    public EmployeeReportSummaryResponse getEmployeeSummary(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        authorizationService.requireEmployeeScope(employeeId);
        Employee employee = getEmployee(employeeId);

        validateDateRange(startDate, endDate);

        List<DailyReport> reports =
                dailyReportRepository
                        .findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
                                employeeId,
                                startDate,
                                endDate
                        );

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

        String employeeName =
                employee.getFirstName()
                        + " "
                        + employee.getLastName();

        return new EmployeeReportSummaryResponse(
                employee.getId(),
                employeeName.trim(),
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

    private Employee getEmployee(UUID employeeId) {

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + employeeId
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