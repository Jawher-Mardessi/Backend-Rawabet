package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.*;
import org.example.rawabet.services.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // ── Register (public) ──────────────────────────────────────────────────
    @PostMapping("/add")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    // ── Create by admin ────────────────────────────────────────────────────
    @PostMapping("/add-with-role")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse createUserByAdmin(@Valid @RequestBody RegisterRequest request) {
        return userService.createUserByAdmin(request);
    }

    // ── Update user (admin) ────────────────────────────────────────────────
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody RegisterRequest request) {
        return userService.updateUser(id, request);
    }

    // ── Update roles ───────────────────────────────────────────────────────
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse updateUserRoles(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRolesRequest request) {
        return userService.updateUserRoles(id, request);
    }

    // ── Delete ─────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // ── Get by id ──────────────────────────────────────────────────────────
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // ── Get all paged ──────────────────────────────────────────────────────
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "20")        int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc")      String direction) {

        Sort sortObj = direction.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        return userService.getAllUsers(PageRequest.of(page, size, sortObj));
    }

    // ── My profile ─────────────────────────────────────────────────────────
    @GetMapping("/me")
    public UserResponse getMyProfile() {
        return userService.getMyProfile();
    }

    @PutMapping("/me/update")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(request);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadMyAvatar(@RequestPart("file") MultipartFile file) {
        return userService.uploadMyAvatar(file);
    }

    @PutMapping("/me/password")
    public String changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return "✅ Mot de passe modifié avec succès";
    }

    // ── BAN TEMPORAIRE ────────────────────────────────────────────────────
    /**
     * Ban temporaire ou permanent avec durée configurable.
     *
     * Corps JSON :
     * {
     *   "banUntil": "2026-05-01T18:00:00",   // null = ban permanent
     *   "reason":   "Comportement inapproprié"
     * }
     */
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse banUser(@PathVariable Long id,
                                @Valid @RequestBody BanRequest request) {
        return userService.banUser(id, request);
    }

    // ── UNBAN ─────────────────────────────────────────────────────────────
    @PutMapping("/{id}/unban")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public UserResponse unbanUser(@PathVariable Long id) {
        return userService.unbanUser(id);
    }
}