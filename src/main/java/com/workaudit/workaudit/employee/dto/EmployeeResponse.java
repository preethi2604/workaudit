package com.workaudit.workaudit.employee.dto;

import lombok.Getter;

@Getter
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String designation;
    private Long userId;
    private String username;
    private String email;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            Long id,
            String employeeCode,
            String firstName,
            String lastName,
            String designation,
            Long userId,
            String username,
            String email) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.designation = designation;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

}