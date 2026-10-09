package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    Optional<DailyReport> findByEmployeeIdAndExpectedDate(String employeeId, LocalDate expectedDate);

    List<DailyReport> findByExpectedDate(LocalDate expectedDate);

    List<DailyReport> findByExpectedDateAndDomainId(LocalDate expectedDate, Long domainId);

    List<DailyReport> findByExpectedDateAndEmployeeIdNot(LocalDate expectedDate, String employeeId);

    List<DailyReport> findByEmployeeIdOrderByExpectedDateDesc(String employeeId);

    List<DailyReport> findByEmployeeIdAndExpectedDateBetweenOrderByExpectedDateDesc(
            String employeeId, LocalDate start, LocalDate end);

    long countByExpectedDate(LocalDate expectedDate);

    long countByExpectedDateAndDomainId(LocalDate expectedDate, Long domainId);
}