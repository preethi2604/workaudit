package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.ValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ValidationResultRepository
        extends JpaRepository<ValidationResult, UUID> {

    Optional<ValidationResult> findByReportId(
            UUID reportId
    );

    boolean existsByReportId(
            UUID reportId
    );
}