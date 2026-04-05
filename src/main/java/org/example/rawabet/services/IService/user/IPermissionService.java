package org.example.rawabet.services.IService.user;

import org.example.rawabet.dto.PermissionResponse;

import java.util.List;

public interface IPermissionService {

    List<PermissionResponse> getAllPermissions();

    void deletePermission(Long id);
}