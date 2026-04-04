package org.example.rawabet.services;

import org.example.rawabet.entities.User;

import java.util.List;

public interface IUserService {

    User addUser(User user);
    User addUserWithRole(User user, String roleName);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    List<User> getAllUsers();
}