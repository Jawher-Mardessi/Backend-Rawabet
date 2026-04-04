package org.example.rawabet.services;

import org.example.rawabet.dto.RoleRequest;
import org.example.rawabet.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}