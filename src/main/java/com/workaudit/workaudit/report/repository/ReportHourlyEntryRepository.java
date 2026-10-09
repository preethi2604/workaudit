package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ReportHourlyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportHourlyEntryRepository extends JpaRepository<ReportHourlyEntry, Long> {
    List<ReportHourlyEntry> findByReportId(Long reportId);
}