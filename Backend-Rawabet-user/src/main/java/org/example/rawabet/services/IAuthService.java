package org.example.rawabet.services;

import org.example.rawabet.entities.User;

public interface IAuthService {

    User login(String email, String password);

    void logout();

    User getAuthenticatedUser();
}