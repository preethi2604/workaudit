package com.workaudit.workaudit.dto;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private String tokenType;
    private UUID employeeId;
    private String email;
    private String role;

    public AuthResponse(
            String token,
            UUID employeeId,
            String email,
            String role
    ) {

        this.token = token;
        this.tokenType = "Bearer";
        this.employeeId = employeeId;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}