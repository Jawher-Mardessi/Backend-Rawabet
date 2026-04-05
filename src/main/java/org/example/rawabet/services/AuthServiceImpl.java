package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 🔐 LOGIN PRO
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    // 🔐 logout (stateless → optionnel)
    @Override
    public void logout() {
        // rien pour l’instant (JWT stateless)
    }

    // 🔐 récupérer user connecté (SAFE)
    @Override
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        return (User) principal;
    }
}