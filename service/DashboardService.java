package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.DashboardResponse;
import com.workaudit.workaudit.dto.DomainDashboardResponse;
import com.workaudit.workaudit.entity.Domain;
import com.workaudit.workaudit.entity.ReportStatus;
import com.workaudit.workaudit.repository.DailyReportRepository;
import com.workaudit.workaudit.repository.DomainRepository;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DomainRepository domainRepository;
    private final DailyReportRepository dailyReportRepository;

    public DashboardService(
            EmployeeRepository employeeRepository,
            DomainRepository domainRepository,
            DailyReportRepository dailyReportRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.domainRepository = domainRepository;
        this.dailyReportRepository = dailyReportRepository;
    }

    public DashboardResponse getSummary() {

        long totalEmployees =
                employeeRepository.count();

        long activeEmployees =
                employeeRepository.countByActiveTrue();

        long totalDomains =
                domainRepository.count();

        long activeDomains =
                domainRepository.countByActiveTrue();

        long totalReports =
                dailyReportRepository.count();

        long submittedReports =
                dailyReportRepository.countByStatus(
                        ReportStatus.SUBMITTED
                );

        long underReviewReports =
                dailyReportRepository.countByStatus(
                        ReportStatus.UNDER_REVIEW
                );

        long approvedReports =
                dailyReportRepository.countByStatus(
                        ReportStatus.APPROVED
                );

        long rejectedReports =
                dailyReportRepository.countByStatus(
                        ReportStatus.REJECTED
                );

        long validationFailedReports =
                dailyReportRepository.countByStatus(
                        ReportStatus.VALIDATION_FAILED
                );

        return new DashboardResponse(
                totalEmployees,
                activeEmployees,
                totalDomains,
                activeDomains,
                totalReports,
                submittedReports,
                underReviewReports,
                approvedReports,
                rejectedReports,
                validationFailedReports
        );
    }

    public List<DomainDashboardResponse> getDomainStatistics() {

        return domainRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::buildDomainStatistics)
                .toList();
    }

    private DomainDashboardResponse buildDomainStatistics(
            Domain domain
    ) {

        long totalReports =
                dailyReportRepository.countByDomainId(
                        domain.getId()
                );

        long submittedReports =
                dailyReportRepository.countByDomainIdAndStatus(
                        domain.getId(),
                        ReportStatus.SUBMITTED
                );

        long underReviewReports =
                dailyReportRepository.countByDomainIdAndStatus(
                        domain.getId(),
                        ReportStatus.UNDER_REVIEW
                );

        long approvedReports =
                dailyReportRepository.countByDomainIdAndStatus(
                        domain.getId(),
                        ReportStatus.APPROVED
                );

        long rejectedReports =
                dailyReportRepository.countByDomainIdAndStatus(
                        domain.getId(),
                        ReportStatus.REJECTED
                );

        long validationFailedReports =
                dailyReportRepository.countByDomainIdAndStatus(
                        domain.getId(),
                        ReportStatus.VALIDATION_FAILED
                );

        return new DomainDashboardResponse(
                domain.getId(),
                domain.getName(),
                domain.getCode(),
                totalReports,
                submittedReports,
                underReviewReports,
                approvedReports,
                rejectedReports,
                validationFailedReports
        );
    }
}