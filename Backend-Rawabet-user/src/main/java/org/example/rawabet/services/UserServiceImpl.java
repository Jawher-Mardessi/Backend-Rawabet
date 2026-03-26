package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // CREATE
    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // UPDATE
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // DELETE
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // GET BY ID
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    // GET ALL
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}