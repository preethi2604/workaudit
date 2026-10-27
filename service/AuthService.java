package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.AuthLoginRequest;
import com.workaudit.workaudit.dto.AuthRegisterRequest;
import com.workaudit.workaudit.dto.AuthResponse;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.EmployeeRole;
import com.workaudit.workaudit.entity.AuditAction;
import com.workaudit.workaudit.repository.EmployeeRepository;
import com.workaudit.workaudit.security.EmployeePrincipal;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuditLogService auditLogService
    ) {

        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    public AuthResponse register(
            AuthRegisterRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        String employeeCode = request.getEmployeeCode()
                .trim();

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException(
                    "An employee with this email already exists"
            );
        }

        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new IllegalStateException(
                    "An employee with this employee code already exists"
            );
        }

        Employee employee = new Employee(
                employeeCode,
                request.getFirstName().trim(),
                request.getLastName().trim(),
                email,
                EmployeeRole.EMPLOYEE
        );

        employee.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        Employee saved =
                employeeRepository.save(employee);

        auditLogService.record(saved.getEmail(), AuditAction.EMPLOYEE_CREATED,
                "EMPLOYEE", saved.getId(), "Employee registered an account");

        EmployeePrincipal principal =
                new EmployeePrincipal(saved);

        String token =
                jwtService.generateToken(
                        principal,
                        saved.getRole().name()
                );

        return new AuthResponse(
                token,
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }

    public AuthResponse login(
            AuthLoginRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

        EmployeePrincipal principal =
                (EmployeePrincipal)
                        authentication.getPrincipal();

        Employee employee =
                principal.getEmployee();

        String token =
                jwtService.generateToken(
                        principal,
                        employee.getRole().name()
                );

        auditLogService.record(employee.getEmail(), AuditAction.LOGIN_SUCCESS,
                "EMPLOYEE", employee.getId(), "Employee logged in successfully");

        return new AuthResponse(
                token,
                employee.getId(),
                employee.getEmail(),
                employee.getRole().name()
        );
    }
}