package org.example.rawabet.services.IService.user;

import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.entities.User;

public interface IAuthService {

    // 🔥 LOGIN + TOKEN (VERSION PRO)
    String login(LoginRequest request);


    User login(String email, String password);

    void logout();

    User getAuthenticatedUser();
}