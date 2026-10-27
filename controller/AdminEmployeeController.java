package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.AdminEmployeeCreateRequest;
import com.workaudit.workaudit.dto.AdminEmployeeResponse;
import com.workaudit.workaudit.dto.UpdateEmployeeRoleRequest;
import com.workaudit.workaudit.dto.UpdateEmployeeStatusRequest;
import com.workaudit.workaudit.service.AdminEmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/employees")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    public AdminEmployeeController(
            AdminEmployeeService adminEmployeeService) {

        this.adminEmployeeService = adminEmployeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminEmployeeResponse createEmployee(
            @Valid @RequestBody AdminEmployeeCreateRequest request) {

        return adminEmployeeService.createEmployee(request);
    }

    @GetMapping
    public List<AdminEmployeeResponse> getAllEmployees() {

        return adminEmployeeService.getAllEmployees();
    }

    @PutMapping("/{employeeId}/role")
    public AdminEmployeeResponse updateRole(
            @PathVariable UUID employeeId,
            @Valid @RequestBody UpdateEmployeeRoleRequest request,
            Authentication authentication) {

        return adminEmployeeService.updateRole(
                employeeId,
                request.getRole(),
                authentication.getName()
        );
    }

    @PutMapping("/{employeeId}/status")
    public AdminEmployeeResponse updateStatus(
            @PathVariable UUID employeeId,
            @Valid @RequestBody UpdateEmployeeStatusRequest request,
            Authentication authentication) {

        return adminEmployeeService.updateStatus(
                employeeId,
                request.getActive(),
                authentication.getName()
        );
    }
}