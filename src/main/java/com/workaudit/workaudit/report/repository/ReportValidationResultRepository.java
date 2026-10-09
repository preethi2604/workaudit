package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ReportValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportValidationResultRepository extends JpaRepository<ReportValidationResult, Long> {
    Optional<ReportValidationResult> findByReportId(Long reportId);
}