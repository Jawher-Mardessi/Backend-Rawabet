package org.example.rawabet.services.ServiceImpl.user;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.example.rawabet.services.IService.user.IAuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 🔥 LOGIN + TOKEN (VERSION PRO)
    @Override
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public User login(String email, String password) {
        return null;
    }

    @Override
    public void logout() {
        // (optionnel avec JWT stateless)
    }

    // 🔐 récupérer user connecté
    @Override
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return (User) authentication.getPrincipal();
    }
}