package org.example.rawabet.services;

import org.example.rawabet.dto.ChangePasswordRequest;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UpdateProfileRequest;
import org.example.rawabet.dto.UserResponse;

import java.util.List;

public interface IUserService {

    // 👤 inscription simple (CLIENT)
    UserResponse register(RegisterRequest request);

    // 🔐 ADMIN crée user avec rôle
    UserResponse createUserByAdmin(RegisterRequest request);

    UserResponse updateUser(Long id, RegisterRequest request);

    void deleteUser(Long id);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    // 👤 profil connecté
    UserResponse getMyProfile();
    UserResponse updateMyProfile(UpdateProfileRequest request);
    void changeMyPassword(ChangePasswordRequest request);
    void banUser(Long id);
    void unbanUser(Long id);
}