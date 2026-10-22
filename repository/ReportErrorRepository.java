package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.ReportError;
import com.workaudit.workaudit.entity.ReportErrorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportErrorRepository
        extends JpaRepository<ReportError, UUID> {

    List<ReportError> findByReportIdOrderByCreatedAtAsc(
            UUID reportId
    );

    List<ReportError> findByReportIdAndErrorType(
            UUID reportId,
            ReportErrorType errorType
    );

    boolean existsByReportIdAndErrorType(
            UUID reportId,
            ReportErrorType errorType
    );
}