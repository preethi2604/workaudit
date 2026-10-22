package com.workaudit.workaudit.controller;

import com.workaudit.workaudit.dto.AssignDomainRequest;
import com.workaudit.workaudit.dto.EmployeeRequest;
import com.workaudit.workaudit.dto.EmployeeResponse;
import com.workaudit.workaudit.service.EmployeeService;
import com.workaudit.workaudit.service.CurrentUserAuthorizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CurrentUserAuthorizationService authorizationService;

    public EmployeeController(
            EmployeeService employeeService,
            CurrentUserAuthorizationService authorizationService
    ) {
        this.employeeService = employeeService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request
    ) {

        EmployeeResponse response =
                employeeService.createEmployee(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<EmployeeResponse>> getEmployees() {

        return ResponseEntity.ok(
                employeeService.getActiveEmployees()
        );
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployee(
            @PathVariable UUID employeeId
    ) {
        authorizationService.requireEmployeeScope(employeeId);
        return ResponseEntity.ok(
                employeeService.getEmployee(employeeId)
        );
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateEmployee(
            @PathVariable UUID employeeId
    ) {

        employeeService.deactivateEmployee(employeeId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{employeeId}/domains")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignDomain(
            @PathVariable UUID employeeId,
            @Valid @RequestBody AssignDomainRequest request
    ) {

        employeeService.assignDomain(
                employeeId,
                request
        );

        return ResponseEntity.status(
                HttpStatus.CREATED
        ).build();
    }

    @DeleteMapping("/{employeeId}/domains/{domainId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeDomain(
            @PathVariable UUID employeeId,
            @PathVariable UUID domainId
    ) {

        employeeService.removeDomain(
                employeeId,
                domainId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/domains")
    public ResponseEntity<List<UUID>> getEmployeeDomains(
            @PathVariable UUID employeeId
    ) {
        authorizationService.requireEmployeeScope(employeeId);
        return ResponseEntity.ok(
                employeeService.getEmployeeDomainIds(
                        employeeId
                )
        );
    }
}