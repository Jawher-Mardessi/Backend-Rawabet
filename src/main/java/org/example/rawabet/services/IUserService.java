package org.example.rawabet.services;

import org.example.rawabet.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    // ── Inscription / Création ─────────────────────────────────────────────
    UserResponse register(RegisterRequest request);
    UserResponse createUserByAdmin(RegisterRequest request);
    UserResponse updateUser(Long id, RegisterRequest request);

    // ── CRUD admin ─────────────────────────────────────────────────────────
    void deleteUser(Long id);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);

    // ── Profil connecté ────────────────────────────────────────────────────
    UserResponse getMyProfile();
    UserResponse updateMyProfile(UpdateProfileRequest request);
    UserResponse uploadMyAvatar(MultipartFile file);
    void changeMyPassword(ChangePasswordRequest request);

    // ── Rôles ─────────────────────────────────────────────────────────────
    UserResponse updateUserRoles(Long id, UpdateUserRolesRequest request);

    // ── Ban / Unban ────────────────────────────────────────────────────────
    /** Ban temporaire ou permanent avec durée et raison. */
    UserResponse banUser(Long id, BanRequest request);

    /** Lève le ban et réactive le compte. */
    UserResponse unbanUser(Long id);
}