package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID>,
        JpaSpecificationExecutor<AuditLog> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByActorIdOrderByCreatedAtDesc(
            UUID actorId
    );

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            UUID entityId
    );
}