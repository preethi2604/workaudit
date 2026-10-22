package com.workaudit.workaudit.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Employee actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuditLog() {
    }

    public AuditLog(
            Employee actor,
            AuditAction action,
            String entityType,
            UUID entityId,
            String description) {

        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Employee getActor() {
        return actor;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}