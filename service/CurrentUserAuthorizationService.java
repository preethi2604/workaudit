package com.workaudit.workaudit.service;

import com.workaudit.workaudit.entity.DailyReport;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.exception.ResourceNotFoundException;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** Centralized ownership checks for employee and report APIs. */
@Service
public class CurrentUserAuthorizationService {

    private final EmployeeRepository employeeRepository;

    public CurrentUserAuthorizationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee currentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null
                || "anonymousUser".equals(auth.getName())) {
            throw new AccessDeniedException("Authentication is required");
        }
        Employee employee = employeeRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee was not found"));
        if (!employee.isActive()) {
            throw new AccessDeniedException("Inactive employees cannot access this resource");
        }
        return employee;
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isManagerOrAdmin() {
        return hasRole("ROLE_MANAGER") || hasRole("ROLE_ADMIN");
    }

    public void requireEmployeeScope(UUID requestedEmployeeId) {
        Employee current = currentEmployee();
        if (!isManagerOrAdmin() && !current.getId().equals(requestedEmployeeId)) {
            throw new AccessDeniedException("You can only access your own employee data");
        }
    }

    public void requireReportReadAccess(DailyReport report) {
        Employee current = currentEmployee();
        if (!isManagerOrAdmin() && !current.getId().equals(report.getEmployee().getId())) {
            throw new AccessDeniedException("You can only access your own reports");
        }
    }

    /** Report edits/submission are owner-only, except for an administrator. */
    public void requireReportMutationAccess(DailyReport report) {
        Employee current = currentEmployee();
        if (!isAdmin() && !current.getId().equals(report.getEmployee().getId())) {
            throw new AccessDeniedException("You can only modify your own reports");
        }
    }

    public void requireCanCreateFor(UUID requestedEmployeeId) {
        Employee current = currentEmployee();
        if (!isAdmin() && !current.getId().equals(requestedEmployeeId)) {
            throw new AccessDeniedException("You can only create reports for yourself");
        }
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
