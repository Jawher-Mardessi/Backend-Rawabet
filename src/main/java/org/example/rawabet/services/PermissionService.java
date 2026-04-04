package org.example.rawabet.services;

import org.example.rawabet.dto.PermissionResponse;

import java.util.List;

public interface PermissionService {

    List<PermissionResponse> getAllPermissions();

    void deletePermission(Long id);
}