package com.workaudit.workaudit.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManagerRequest {

    @NotBlank(message = "Manager name is required")
    @Size(max = 150, message = "Manager name cannot exceed 150 characters")
    private String name;

    public ManagerRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}