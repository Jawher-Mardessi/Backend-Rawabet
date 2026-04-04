package org.example.rawabet.services;

import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UserResponse;

import java.util.List;

public interface IUserService {

    UserResponse addUser(RegisterRequest request);

    UserResponse addUserWithRole(RegisterRequest request);

    UserResponse updateUser(Long id, RegisterRequest request);

    void deleteUser(Long id);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();
}