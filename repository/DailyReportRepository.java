package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyReportRepository
        extends JpaRepository<DailyReport, UUID> {

    // ============================================================
    // REPORT CREATION / DUPLICATE CHECK
    // ============================================================

    Optional<DailyReport> findByEmployeeIdAndReportDate(
            UUID employeeId,
            LocalDate reportDate
    );

    boolean existsByEmployeeIdAndReportDate(
            UUID employeeId,
            LocalDate reportDate
    );


    // ============================================================
    // EMPLOYEE REPORTS
    // ============================================================

    List<DailyReport> findByEmployeeIdOrderByReportDateDesc(
            UUID employeeId
    );

    List<DailyReport>
    findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate
    );


    // ============================================================
    // DATE BASED REPORTS
    // ============================================================

    List<DailyReport> findByReportDateOrderBySubmittedAtDesc(
            LocalDate reportDate
    );

    /*
     * Required by existing ReportService.
     */
    List<DailyReport> findByReportDate(
            LocalDate reportDate
    );


    // ============================================================
    // DOMAIN BASED REPORTS
    // ============================================================

    List<DailyReport>
    findByDomainIdAndReportDateOrderBySubmittedAtDesc(
            UUID domainId,
            LocalDate reportDate
    );

    /*
     * Required by existing ReportService.
     */
    List<DailyReport> findByDomainIdAndReportDate(
            UUID domainId,
            LocalDate reportDate
    );


    // ============================================================
    // COUNT QUERIES
    // ============================================================

    long countByEmployeeId(
            UUID employeeId
    );

    long countByDomainId(
            UUID domainId
    );

    long countByReportDate(
            LocalDate reportDate
    );

    long countByStatus(
            ReportStatus status
    );

    long countByDomainIdAndStatus(
            UUID domainId,
            ReportStatus status
    );

    List<DailyReport> findByReportDateBetweenOrderByReportDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

    List<DailyReport>
    findByDomainIdAndReportDateBetweenOrderByReportDateAsc(
            UUID domainId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<DailyReport>
    findByEmployeeIdAndReportDateBetweenOrderByReportDateAsc(
            UUID employeeId,
            LocalDate startDate,
            LocalDate endDate
    );
}