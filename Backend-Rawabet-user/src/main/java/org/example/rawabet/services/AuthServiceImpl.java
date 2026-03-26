package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public String loginAndGenerateToken(String email, String password) {
        User user = login(email, password);
        return jwtService.generateToken(user.getEmail());
    }

    @Override
    public void logout() {}

    @Override
    public User getAuthenticatedUser() {
        return null;
    }
}