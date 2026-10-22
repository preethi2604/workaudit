package com.workaudit.workaudit.employee.repository;

import com.workaudit.workaudit.employee.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCaseAndIdNot(
            String employeeCode,
            Long id
    );

    boolean existsByUserId(Long userId);
}