package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.AdminEmployeeCreateRequest;
import com.workaudit.workaudit.dto.AdminEmployeeResponse;
import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.EmployeeRole;
import com.workaudit.workaudit.exception.ConflictException;
import com.workaudit.workaudit.exception.ResourceNotFoundException;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AdminEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AdminEmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {

        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    /**
     * Create a new employee/manager/admin account.
     */
    public AdminEmployeeResponse createEmployee(
            AdminEmployeeCreateRequest request) {

        if (employeeRepository.existsByEmailIgnoreCase(
                request.getEmail())) {

            throw new ConflictException(
                    "Employee with this email already exists");
        }

        if (employeeRepository.existsByEmployeeCode(
                request.getEmployeeCode())) {

            throw new ConflictException(
                    "Employee with this employee code already exists");
        }

        Employee employee = new Employee();

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));
        employee.setRole(request.getRole());
        employee.setActive(true);

        Employee savedEmployee =
                employeeRepository.save(employee);

        // Record audit log after successful creation.
        auditLogService.record(
                getCurrentAdminEmail(),
                AuditAction.EMPLOYEE_CREATED,
                "EMPLOYEE",
                savedEmployee.getId(),
                "Created employee "
                        + savedEmployee.getEmployeeCode()
                        + " with role "
                        + savedEmployee.getRole()
        );

        return toResponse(savedEmployee);
    }

    /**
     * Get all employees.
     */
    @Transactional(readOnly = true)
    public List<AdminEmployeeResponse> getAllEmployees() {

        return employeeRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Change an employee's role.
     */
    public AdminEmployeeResponse updateRole(
            UUID employeeId,
            EmployeeRole newRole,
            String currentAdminEmail) {

        Employee employee = getEmployee(employeeId);

        preventSelfModification(
                employee,
                currentAdminEmail
        );

        EmployeeRole previousRole = employee.getRole();

        employee.setRole(newRole);

        Employee updatedEmployee =
                employeeRepository.save(employee);

        // Record only the successful role change.
        auditLogService.record(
                getCurrentAdminEmail(),
                AuditAction.EMPLOYEE_ROLE_CHANGED,
                "EMPLOYEE",
                updatedEmployee.getId(),
                "Changed employee "
                        + updatedEmployee.getEmployeeCode()
                        + " role from "
                        + previousRole
                        + " to "
                        + newRole
        );

        return toResponse(updatedEmployee);
    }

    /**
     * Activate or deactivate an employee.
     */
    public AdminEmployeeResponse updateStatus(
            UUID employeeId,
            boolean active,
            String currentAdminEmail) {

        Employee employee = getEmployee(employeeId);

        preventSelfModification(
                employee,
                currentAdminEmail
        );

        boolean previousStatus = employee.isActive();

        employee.setActive(active);

        Employee updatedEmployee =
                employeeRepository.save(employee);

        AuditAction action = active
                ? AuditAction.EMPLOYEE_ACTIVATED
                : AuditAction.EMPLOYEE_DEACTIVATED;

        // Record only the successful status change.
        auditLogService.record(
                getCurrentAdminEmail(),
                action,
                "EMPLOYEE",
                updatedEmployee.getId(),
                (active ? "Activated employee "
                        : "Deactivated employee ")
                        + updatedEmployee.getEmployeeCode()
                        + " (previous status: "
                        + previousStatus
                        + ")"
        );

        return toResponse(updatedEmployee);
    }

    /**
     * Get the email of the currently authenticated user
     * from the JWT/Spring Security context.
     */
    private String getCurrentAdminEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        return authentication.getName();
    }

    /**
     * Find an employee by ID.
     *
     * Throws 404 through GlobalExceptionHandler
     * when the employee does not exist.
     */
    private Employee getEmployee(UUID employeeId) {

        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: "
                                        + employeeId
                        )
                );
    }

    /**
     * Prevent an admin from modifying their own account.
     */
    private void preventSelfModification(
            Employee employee,
            String currentAdminEmail) {

        if (employee.getEmail()
                .equalsIgnoreCase(currentAdminEmail)) {

            throw new ConflictException(
                    "Admin cannot modify their own account"
            );
        }
    }

    /**
     * Convert Employee entity to API response DTO.
     */
    private AdminEmployeeResponse toResponse(
            Employee employee) {

        AdminEmployeeResponse response =
                new AdminEmployeeResponse();

        response.setId(employee.getId());
        response.setEmployeeCode(
                employee.getEmployeeCode()
        );
        response.setFirstName(
                employee.getFirstName()
        );
        response.setLastName(
                employee.getLastName()
        );
        response.setEmail(
                employee.getEmail()
        );
        response.setRole(
                employee.getRole()
        );
        response.setActive(
                employee.isActive()
        );

        return response;
    }
}