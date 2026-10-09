package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ReportDuplicate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDuplicateRepository extends JpaRepository<ReportDuplicate, Long> {
    List<ReportDuplicate> findByReportId(Long reportId);
    void deleteByReportId(Long reportId);
}