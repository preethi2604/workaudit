package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.AuditLogResponse;
import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.entity.AuditLog;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.repository.AuditLogRepository;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final EmployeeRepository employeeRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            EmployeeRepository employeeRepository) {

        this.auditLogRepository = auditLogRepository;
        this.employeeRepository = employeeRepository;
    }

    public void record(
            String actorEmail,
            AuditAction action,
            String entityType,
            UUID entityId,
            String description) {

        Employee actor = null;

        if (actorEmail != null && !actorEmail.isBlank()) {

            actor = employeeRepository
                    .findByEmailIgnoreCase(actorEmail)
                    .orElse(null);
        }

        AuditLog auditLog = new AuditLog(
                actor,
                action,
                entityType,
                entityId,
                description
        );

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByActor(
            UUID actorId) {

        return auditLogRepository
                .findByActorIdOrderByCreatedAtDesc(actorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByEntity(
            String entityType,
            UUID entityId) {

        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        entityType,
                        entityId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> searchAuditLogs(
            int page,
            int size,
            AuditAction action,
            String entityType,
            UUID actorId,
            Instant from,
            Instant to) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Start timestamp cannot be after end timestamp"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<AuditLog> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (action != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("action"),
                                    action
                            )
            );
        }

        if (entityType != null && !entityType.isBlank()) {
            String normalizedEntityType =
                    entityType.trim().toUpperCase(
                            java.util.Locale.ROOT
                    );

            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    cb.upper(root.get("entityType")),
                                    normalizedEntityType
                            )
            );
        }

        if (actorId != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("actor").get("id"),
                                    actorId
                            )
            );
        }

        if (from != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("createdAt"),
                                    from
                            )
            );
        }

        if (to != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("createdAt"),
                                    to
                            )
            );
        }

        return auditLogRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    private AuditLogResponse toResponse(
            AuditLog auditLog) {

        AuditLogResponse response =
                new AuditLogResponse();

        response.setId(auditLog.getId());

        if (auditLog.getActor() != null) {
            response.setActorId(
                    auditLog.getActor().getId()
            );

            response.setActorEmail(
                    auditLog.getActor().getEmail()
            );
        }

        response.setAction(auditLog.getAction());
        response.setEntityType(
                auditLog.getEntityType()
        );
        response.setEntityId(
                auditLog.getEntityId()
        );
        response.setDescription(
                auditLog.getDescription()
        );
        response.setCreatedAt(
                auditLog.getCreatedAt()
        );

        return response;
    }
}
