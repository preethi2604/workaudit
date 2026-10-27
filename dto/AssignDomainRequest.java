package com.workaudit.workaudit.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AssignDomainRequest {

    @NotNull(message = "Domain ID is required")
    private UUID domainId;

    public AssignDomainRequest() {
    }

    public UUID getDomainId() {
        return domainId;
    }

    public void setDomainId(UUID domainId) {
        this.domainId = domainId;
    }
}