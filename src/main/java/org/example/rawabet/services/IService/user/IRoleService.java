package org.example.rawabet.services.IService.user;

import org.example.rawabet.dto.RoleRequest;
import org.example.rawabet.dto.RoleResponse;

import java.util.List;

public interface IRoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}