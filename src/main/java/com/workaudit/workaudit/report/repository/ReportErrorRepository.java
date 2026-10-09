package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ErrorType;
import com.workaudit.workaudit.report.entity.ReportError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportErrorRepository extends JpaRepository<ReportError, Long> {

    List<ReportError> findByReportId(Long reportId);

    void deleteByReportId(Long reportId);

    List<ReportError> findByReport_ExpectedDateAndErrorType(LocalDate expectedDate, ErrorType errorType);

    List<ReportError> findByReport_ExpectedDate(LocalDate expectedDate);
}