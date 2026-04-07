package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UserResponse;
import org.example.rawabet.services.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.rawabet.dto.UpdateProfileRequest;
import org.example.rawabet.dto.ChangePasswordRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // =========================
    // 👤 REGISTER (CLIENT)
    // =========================
    @PostMapping("/add")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    // =========================
    // 🔐 CREATE USER (ADMIN)
    // =========================
    @PostMapping("/add-with-role")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse createUserByAdmin(@Valid @RequestBody RegisterRequest request) {
        return userService.createUserByAdmin(request);
    }

    // =========================
    // ✏️ UPDATE USER
    // =========================
    @PutMapping("/update")
    public UserResponse updateUser(@Valid @RequestBody RegisterRequest request,
                                   @RequestParam Long id) {
        return userService.updateUser(id, request);
    }

    // =========================
    // ❌ DELETE USER
    // =========================
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // =========================
    // 🔍 GET USER BY ID
    // =========================
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // =========================
    // 📋 GET ALL USERS
    // =========================
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // =========================
// 👤 GET MY PROFILE
// =========================
    @GetMapping("/me")
    public UserResponse getMyProfile() {
        return userService.getMyProfile();
    }

    // =========================
// ✏️ UPDATE MY PROFILE
// =========================
    @PutMapping("/me/update")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(request);
    }

    // =========================
// 🔐 CHANGE PASSWORD
// =========================
    @PutMapping("/me/password")
    public String changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return "✅ Mot de passe modifié avec succès";
    }

    // 🚫 BAN USER
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public String banUser(@PathVariable Long id) {
        userService.banUser(id);
        return "✅ User " + id + " banni avec succès";
    }

    // ✅ UNBAN USER
    @PutMapping("/{id}/unban")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public String unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return "✅ User " + id + " réactivé avec succès";
    }
}