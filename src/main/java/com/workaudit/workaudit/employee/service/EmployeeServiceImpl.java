package com.workaudit.workaudit.employee.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;

import com.workaudit.workaudit.employee.dto.EmployeeRequest;
import com.workaudit.workaudit.employee.dto.EmployeeResponse;
import com.workaudit.workaudit.employee.entity.Employee;
import com.workaudit.workaudit.employee.repository.EmployeeRepository;

import com.workaudit.workaudit.user.entity.User;
import com.workaudit.workaudit.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository) {

        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        String employeeCode = request.getEmployeeCode().trim();

        if (employeeRepository.existsByEmployeeCodeIgnoreCase(employeeCode)) {
            throw new BadRequestException(
                    "Employee code already exists"
            );
        }

        User user = findUserById(request.getUserId());

        if (employeeRepository.existsByUserId(user.getId())) {
            throw new BadRequestException(
                    "This user is already assigned to an employee"
            );
        }

        Employee employee = new Employee();

        employee.setEmployeeCode(employeeCode);
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setDesignation(request.getDesignation().trim());
        employee.setUser(user);

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = findEmployeeById(id);

        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {

        Employee employee = findEmployeeById(id);

        String employeeCode = request.getEmployeeCode().trim();

        if (employeeRepository.existsByEmployeeCodeIgnoreCaseAndIdNot(
                employeeCode, id)) {

            throw new BadRequestException(
                    "Employee code already exists"
            );
        }

        User user = findUserById(request.getUserId());

        boolean assignedToAnotherEmployee =
                employeeRepository.existsByUserId(user.getId())
                        && !employee.getUser().getId().equals(user.getId());

        if (assignedToAnotherEmployee) {
            throw new BadRequestException(
                    "This user is already assigned to another employee"
            );
        }

        employee.setEmployeeCode(employeeCode);
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setDesignation(request.getDesignation().trim());
        employee.setUser(user);

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = findEmployeeById(id);

        employeeRepository.delete(employee);
    }

    private Employee findEmployeeById(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with ID: " + id
                        )
                );
    }

    private User findUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        User user = employee.getUser();

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDesignation(),
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}