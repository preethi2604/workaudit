package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.EmployeeDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeDomainRepository
        extends JpaRepository<EmployeeDomain, UUID> {

    Optional<EmployeeDomain> findByEmployeeIdAndDomainId(
            UUID employeeId,
            UUID domainId
    );

    List<EmployeeDomain> findByEmployeeIdAndActiveTrue(
            UUID employeeId
    );

    List<EmployeeDomain> findByDomainIdAndActiveTrue(
            UUID domainId
    );

    boolean existsByEmployeeIdAndDomainId(
            UUID employeeId,
            UUID domainId
    );
}