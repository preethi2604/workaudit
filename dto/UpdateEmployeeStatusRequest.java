package com.workaudit.workaudit.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateEmployeeStatusRequest {

    @NotNull
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}