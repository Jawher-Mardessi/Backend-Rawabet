package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.PermissionResponse;
import org.example.rawabet.services.IService.user.IPermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    // ✅ GET ALL
    @GetMapping("/all")
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    // ❌ DELETE
    @DeleteMapping("/delete/{id}")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}