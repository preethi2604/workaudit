package com.workaudit.workaudit.service;

import com.workaudit.workaudit.dto.AssignDomainRequest;
import com.workaudit.workaudit.dto.EmployeeRequest;
import com.workaudit.workaudit.dto.EmployeeResponse;
import com.workaudit.workaudit.entity.Domain;
import com.workaudit.workaudit.entity.Employee;
import com.workaudit.workaudit.entity.EmployeeDomain;
import com.workaudit.workaudit.repository.DomainRepository;
import com.workaudit.workaudit.repository.EmployeeDomainRepository;
import com.workaudit.workaudit.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DomainRepository domainRepository;
    private final EmployeeDomainRepository employeeDomainRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DomainRepository domainRepository,
            EmployeeDomainRepository employeeDomainRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.domainRepository = domainRepository;
        this.employeeDomainRepository = employeeDomainRepository;
    }

    public EmployeeResponse createEmployee(
            EmployeeRequest request
    ) {

        String employeeCode =
                request.getEmployeeCode().trim();

        String email =
                request.getEmail().trim().toLowerCase();

        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new IllegalArgumentException(
                    "Employee code already exists: " +
                            employeeCode
            );
        }

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Employee email already exists: " +
                            email
            );
        }

        Employee employee = new Employee(
                employeeCode,
                request.getFirstName().trim(),
                request.getLastName().trim(),
                email,
                request.getRole()
        );

        return EmployeeResponse.fromEntity(
                employeeRepository.save(employee)
        );
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getActiveEmployees() {

        return employeeRepository
                .findByActiveTrueOrderByFirstNameAsc()
                .stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(UUID employeeId) {

        Employee employee = getEmployeeEntity(employeeId);

        return EmployeeResponse.fromEntity(employee);
    }

    public void deactivateEmployee(UUID employeeId) {

        Employee employee = getEmployeeEntity(employeeId);

        employee.setActive(false);

        employeeRepository.save(employee);
    }

    public void assignDomain(
            UUID employeeId,
            AssignDomainRequest request
    ) {

        Employee employee = getEmployeeEntity(employeeId);

        Domain domain = domainRepository
                .findById(request.getDomainId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domain not found: " +
                                        request.getDomainId()
                        )
                );

        if (!employee.isActive()) {
            throw new IllegalStateException(
                    "Cannot assign a domain to an inactive employee"
            );
        }

        if (!domain.isActive()) {
            throw new IllegalStateException(
                    "Cannot assign an inactive domain"
            );
        }

        if (employeeDomainRepository
                .existsByEmployeeIdAndDomainId(
                        employeeId,
                        domain.getId()
                )) {

            throw new IllegalArgumentException(
                    "Employee is already assigned to this domain"
            );
        }

        EmployeeDomain employeeDomain =
                new EmployeeDomain(employee, domain);

        employeeDomainRepository.save(employeeDomain);
    }

    public void removeDomain(
            UUID employeeId,
            UUID domainId
    ) {

        EmployeeDomain employeeDomain =
                employeeDomainRepository
                        .findByEmployeeIdAndDomainId(
                                employeeId,
                                domainId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employee-domain assignment not found"
                                )
                        );

        employeeDomain.setActive(false);

        employeeDomainRepository.save(employeeDomain);
    }

    @Transactional(readOnly = true)
    public List<UUID> getEmployeeDomainIds(
            UUID employeeId
    ) {

        getEmployeeEntity(employeeId);

        return employeeDomainRepository
                .findByEmployeeIdAndActiveTrue(employeeId)
                .stream()
                .map(employeeDomain ->
                        employeeDomain
                                .getDomain()
                                .getId()
                )
                .toList();
    }

    private Employee getEmployeeEntity(
            UUID employeeId
    ) {

        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " +
                                        employeeId
                        )
                );
    }
}