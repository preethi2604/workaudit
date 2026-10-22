package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.ReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportHistoryRepository
        extends JpaRepository<ReportHistory, UUID> {

    List<ReportHistory> findByReportIdOrderByChangedAtDesc(
            UUID reportId
    );

    List<ReportHistory> findByChangedByOrderByChangedAtDesc(
            UUID changedBy
    );
}