package com.workaudit.workaudit.role.service;

import com.workaudit.workaudit.role.dto.RoleRequest;
import com.workaudit.workaudit.role.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}