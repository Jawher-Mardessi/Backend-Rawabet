package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RoleRequest;
import org.example.rawabet.dto.RoleResponse;
import org.example.rawabet.services.IService.user.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    // 🔐 GET ALL ROLES (ADMIN ONLY)
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @GetMapping("/all")
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    // 🔐 CREATE ROLE
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @PostMapping("/create")
    public RoleResponse createRole(@Valid @RequestBody RoleRequest request) {
        return roleService.createRole(request);
    }

    // 🔐 UPDATE ROLE
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @PutMapping("/update/{id}")
    public RoleResponse updateRole(@PathVariable Long id,
                                   @Valid @RequestBody RoleRequest request) {
        return roleService.updateRole(id, request);
    }

    // 🔐 DELETE ROLE
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @DeleteMapping("/delete/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}