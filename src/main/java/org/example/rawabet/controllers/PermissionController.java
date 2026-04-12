package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.PermissionResponse;
import org.example.rawabet.services.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    // ✅ GET ALL
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    // ❌ DELETE
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}