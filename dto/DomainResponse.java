package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.Domain;

import java.time.Instant;
import java.util.UUID;

public class DomainResponse {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public DomainResponse() {
    }

    public DomainResponse(
            UUID id,
            String name,
            String code,
            String description,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DomainResponse fromEntity(Domain domain) {

        return new DomainResponse(
                domain.getId(),
                domain.getName(),
                domain.getCode(),
                domain.getDescription(),
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}