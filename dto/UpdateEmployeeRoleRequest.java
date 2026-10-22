package com.workaudit.workaudit.dto;

import com.workaudit.workaudit.entity.EmployeeRole;
import jakarta.validation.constraints.NotNull;

public class UpdateEmployeeRoleRequest {

    @NotNull
    private EmployeeRole role;

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }
}