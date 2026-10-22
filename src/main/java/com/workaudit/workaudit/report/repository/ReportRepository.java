package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByEmployeeId(Long employeeId);

    List<Report> findByReportDate(LocalDate reportDate);

    List<Report> findByEmployeeIdAndReportDate(
            Long employeeId,
            LocalDate reportDate
    );

    List<Report> findByReportDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );
}