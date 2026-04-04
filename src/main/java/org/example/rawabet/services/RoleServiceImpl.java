package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RoleRequest;
import org.example.rawabet.dto.RoleResponse;
import org.example.rawabet.entities.Permission;
import org.example.rawabet.entities.Role;
import org.example.rawabet.repositories.PermissionRepository;
import org.example.rawabet.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    // =========================
    // ✅ GET ALL ROLES
    // =========================
    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // ✅ CREATE ROLE (SECURED)
    // =========================
    @Override
    public RoleResponse createRole(RoleRequest request) {

        validateRequest(request);

        String roleName = request.getName().toUpperCase();

        // ❌ Role already exists
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists");
        }

        // 🔐 BLOCK ADMIN_MANAGE
        if (request.getPermissions().contains("ADMIN_MANAGE")) {
            throw new RuntimeException("Cannot assign ADMIN_MANAGE permission");
        }

        // 🔐 FETCH PERMISSIONS
        List<Permission> permissions =
                permissionRepository.findByNameIn(request.getPermissions());

        if (permissions.isEmpty()) {
            throw new RuntimeException("No valid permissions found");
        }

        Role role = new Role();
        role.setName(roleName);
        role.setPermissions(permissions);

        return mapToResponse(roleRepository.save(role));
    }

    // =========================
    // ✅ UPDATE ROLE (SECURED)
    // =========================
    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {

        validateRequest(request);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 🔐 PROTECT SUPER ADMIN
        if (Objects.equals(role.getName(), "SUPER_ADMIN")) {
            throw new RuntimeException("Cannot modify SUPER_ADMIN role");
        }

        // 🔐 BLOCK ADMIN_MANAGE
        if (request.getPermissions().contains("ADMIN_MANAGE")) {
            throw new RuntimeException("Cannot assign ADMIN_MANAGE permission");
        }

        List<Permission> permissions =
                permissionRepository.findByNameIn(request.getPermissions());

        if (permissions.isEmpty()) {
            throw new RuntimeException("No valid permissions found");
        }

        role.setPermissions(permissions);

        return mapToResponse(roleRepository.save(role));
    }

    // =========================
    // ✅ DELETE ROLE (SECURED)
    // =========================
    @Override
    public void deleteRole(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 🔐 PROTECT SUPER ADMIN
        if (Objects.equals(role.getName(), "SUPER_ADMIN")) {
            throw new RuntimeException("Cannot delete SUPER_ADMIN");
        }

        roleRepository.delete(role);
    }

    // =========================
    // 🔥 VALIDATION METHOD
    // =========================
    private void validateRequest(RoleRequest request) {

        if (request == null) {
            throw new RuntimeException("Request cannot be null");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Role name is required");
        }

        if (request.getPermissions() == null || request.getPermissions().isEmpty()) {
            throw new RuntimeException("Permissions list cannot be empty");
        }
    }

    // =========================
    // 🔥 MAPPING ENTITY → DTO
    // =========================
    private RoleResponse mapToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(
                        role.getPermissions()
                                .stream()
                                .map(Permission::getName)
                                .toList()
                )
                .build();
    }
}