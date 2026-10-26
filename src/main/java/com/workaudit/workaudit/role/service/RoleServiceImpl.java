package com.workaudit.workaudit.role.service;

import com.workaudit.workaudit.common.BadRequestException;
import com.workaudit.workaudit.common.ResourceNotFoundException;
import com.workaudit.workaudit.role.dto.RoleRequest;
import com.workaudit.workaudit.role.dto.RoleResponse;
import com.workaudit.workaudit.role.entity.Role;
import com.workaudit.workaudit.role.repository.RoleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Role already exists"
            );
        }

        Role role = new Role();

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role savedRole = roleRepository.save(role);

        return convertToResponse(savedRole);
    }

    @Override
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: " + id
                        ));

        return convertToResponse(role);
    }

    @Override
    public RoleResponse updateRole(
            Long id,
            RoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: " + id
                        ));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return convertToResponse(
                roleRepository.save(role)
        );
    }

    @Override
    public void deleteRole(Long id) {

        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Role not found with id: " + id
            );
        }

        roleRepository.deleteById(id);
    }

    private RoleResponse convertToResponse(Role role) {

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}