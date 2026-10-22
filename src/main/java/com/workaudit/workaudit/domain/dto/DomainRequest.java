package com.workaudit.workaudit.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DomainRequest {

    @NotBlank(message = "Domain name is required")
    @Size(max = 150, message = "Domain name cannot exceed 150 characters")
    private String name;

    public DomainRequest() {
    }

    public DomainRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}