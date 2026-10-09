package com.workaudit.workaudit.report.repository;

import com.workaudit.workaudit.report.entity.ValidationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationConfigRepository extends JpaRepository<ValidationConfig, Long> {
    Optional<ValidationConfig> findByConfigKey(String configKey);
}