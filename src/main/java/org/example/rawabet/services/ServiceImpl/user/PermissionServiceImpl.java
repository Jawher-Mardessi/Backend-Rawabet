package org.example.rawabet.services.ServiceImpl.user;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.PermissionResponse;
import org.example.rawabet.entities.Permission;
import org.example.rawabet.repositories.PermissionRepository;
import org.example.rawabet.services.IService.user.IPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    // 🔥 mapping DTO
    private PermissionResponse mapToResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .module(permission.getModule())
                .action(permission.getAction())
                .build();
    }
}