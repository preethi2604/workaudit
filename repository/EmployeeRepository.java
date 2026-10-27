package com.workaudit.workaudit.repository;

import com.workaudit.workaudit.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workaudit.workaudit.entity.EmployeeRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository
        extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmailIgnoreCase(String email);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeCode(String employeeCode);

    List<Employee> findByActiveTrueOrderByFirstNameAsc();

    long countByActiveTrue();

    List<Employee> findByRoleAndActiveTrue(
            EmployeeRole role
    );
}