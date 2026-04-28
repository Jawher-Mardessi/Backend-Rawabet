package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import java.util.Locale;
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
    private final MessageSource messageSource;

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
            throw new RuntimeException(messageSource.getMessage("role.already.exists", new Object[]{roleName}, Locale.ENGLISH));
        }

        if ("SUPER_ADMIN".equals(roleName)) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.create.superadmin", null, Locale.ENGLISH));
        }

        List<String> permissionNames = request.getPermissions().stream()
                .map(String::toUpperCase)
                .distinct()
                .toList();

        // 🔐 BLOCK ADMIN_MANAGE
        if (permissionNames.contains("ADMIN_MANAGE")) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.assign.admin_manage", null, Locale.ENGLISH));
        }

        // 🔐 FETCH PERMISSIONS
        List<Permission> permissions =
                permissionRepository.findByNameIn(permissionNames);

        if (permissions.size() != permissionNames.size()) {
            throw new RuntimeException(messageSource.getMessage("role.permissions.invalid", null, Locale.ENGLISH));
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
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("role.notfound", new Object[]{id}, Locale.ENGLISH)));

        // 🔐 PROTECT SUPER ADMIN
        if (Objects.equals(role.getName(), "SUPER_ADMIN")) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.modify.superadmin", null, Locale.ENGLISH));
        }

        List<String> permissionNames = request.getPermissions().stream()
                .map(String::toUpperCase)
                .distinct()
                .toList();

        // 🔐 BLOCK ADMIN_MANAGE
        if (permissionNames.contains("ADMIN_MANAGE")) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.assign.admin_manage", null, Locale.ENGLISH));
        }

        List<Permission> permissions =
                permissionRepository.findByNameIn(permissionNames);

        if (permissions.size() != permissionNames.size()) {
            throw new RuntimeException(messageSource.getMessage("role.permissions.invalid", null, Locale.ENGLISH));
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
            .orElseThrow(() -> new RuntimeException(messageSource.getMessage("role.notfound", new Object[]{id}, Locale.ENGLISH)));

        // 🔐 PROTECT SUPER ADMIN
        if (Objects.equals(role.getName(), "SUPER_ADMIN")) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.delete.superadmin", null, Locale.ENGLISH));
        }

        if ("CLIENT".equals(role.getName())) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.delete.client", null, Locale.ENGLISH));
        }

        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new RuntimeException(messageSource.getMessage("role.cannot.delete.assigned", null, Locale.ENGLISH));
        }

        roleRepository.delete(role);
    }

    // =========================
    // 🔥 VALIDATION METHOD
    // =========================
    private void validateRequest(RoleRequest request) {

        if (request == null) {
            throw new RuntimeException(messageSource.getMessage("role.request.null", null, Locale.ENGLISH));
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException(messageSource.getMessage("role.name.required", null, Locale.ENGLISH));
        }

        if (request.getPermissions() == null || request.getPermissions().isEmpty()) {
            throw new RuntimeException(messageSource.getMessage("role.permissions.required", null, Locale.ENGLISH));
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