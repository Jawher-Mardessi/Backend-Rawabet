package org.example.rawabet.services;

import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.entities.User;

public interface IAuthService {

    AuthResponse login(LoginRequest request);

    void logout();

    User getAuthenticatedUser();
}