package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.ChangePasswordRequest;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UpdateProfileRequest;
import org.example.rawabet.dto.UpdateUserRolesRequest;
import org.example.rawabet.dto.UserResponse;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.EmailVerificationToken;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.Level;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.example.rawabet.repositories.EmailVerificationTokenRepository;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarteFideliteRepository carteRepository;
    private final IAuthService authService;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    // =========================
    // 👤 REGISTER (CLIENT)
    // =========================
    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        checkEmail(request.getEmail());

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(clientRole));
        user.setActive(true); // ✅ inactif jusqu'à confirmation email

        User savedUser = userRepository.save(user);
        carteRepository.save(createCarte(savedUser));
        // sendVerificationEmail(savedUser);
        return mapToResponse(savedUser);
    }

    // =========================
    // 🔐 CREATE USER (ADMIN)
    // =========================
    @Override
    @Transactional
    public UserResponse createUserByAdmin(RegisterRequest request) {
        checkEmail(request.getEmail());

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new RuntimeException("Roles are required");
        }

        List<String> roleNames = request.getRoles()
                .stream()
                .map(String::toUpperCase)
                .toList();

        if (roleNames.contains("SUPER_ADMIN")) {
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        }

        List<Role> roles = roleRepository.findByNameIn(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("Some roles are invalid");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        user.setActive(true); // ✅ inactif jusqu'à confirmation email

        User savedUser = userRepository.save(user);
        carteRepository.save(createCarte(savedUser));
        // sendVerificationEmail(savedUser);
        return mapToResponse(savedUser);
    }

    // =========================
    // ✏️ UPDATE USER
    // =========================
    @Override
    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNom(request.getNom());

        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToResponse(userRepository.save(user));
    }

    // =========================
    // ❌ DELETE USER
    // =========================
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        carteRepository.findByUser(user)
                .ifPresent(carteRepository::delete);

        userRepository.delete(user);
    }

    // =========================
    // 🔍 GET USER BY ID
    // =========================
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    // =========================
    // 📋 GET ALL USERS
    // =========================
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // 👤 GET MY PROFILE
    // =========================
    @Override
    public UserResponse getMyProfile() {
        User user = authService.getAuthenticatedUser();
        return mapToResponse(user);
    }

    // =========================
    // ✏️ UPDATE MY PROFILE
    // =========================
    @Override
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        User user = authService.getAuthenticatedUser();

        user.setNom(request.getNom());

        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail());
        }

        return mapToResponse(userRepository.save(user));
    }

    // =========================
    // 🔐 CHANGE MY PASSWORD
    // =========================
    @Override
    public void changeMyPassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    // =========================
    // 🔐 UPDATE USER ROLES (ADMIN)
    // =========================
    @Override
    @Transactional
    public UserResponse updateUserRoles(Long id, UpdateUserRolesRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean targetIsSuperAdmin = user.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getName()));
        if (targetIsSuperAdmin) {
            throw new RuntimeException("Impossible de modifier les roles d'un SUPER_ADMIN");
        }

        List<String> requestedRoleNames = request.getRoles().stream()
                .map(String::toUpperCase)
                .distinct()
                .toList();

        if (requestedRoleNames.contains("SUPER_ADMIN")) {
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        }

        List<Role> roles = roleRepository.findByNameIn(requestedRoleNames);
        if (roles.size() != requestedRoleNames.size()) {
            throw new RuntimeException("Some roles are invalid");
        }

        user.setRoles(roles);
        user.setTokenVersion(user.getTokenVersion() + 1);
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    // =========================
    // 🚫 BAN USER
    // =========================
    @Override
    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User déjà banni");
        }

        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("SUPER_ADMIN"));
        if (isSuperAdmin) {
            throw new RuntimeException("Impossible de bannir un SUPER_ADMIN");
        }

        user.setActive(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    // =========================
    // ✅ UNBAN USER
    // =========================
    @Override
    public void unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isActive()) {
            throw new RuntimeException("User déjà actif");
        }

        user.setActive(true);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    // =========================
    // 🔐 EMAIL CHECK
    // =========================
    private void checkEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
    }

    // =========================
    // 💳 CREATE CARTE
    // =========================
    private CarteFidelite createCarte(User user) {
        return CarteFidelite.builder()
                .user(user)
                .points(0)
                .level(Level.SILVER)
                .dateExpiration(LocalDate.now().plusYears(1))
                .build();
    }

    // =========================
    // 🔥 MAPPING
    // =========================

    private UserResponse mapToResponse(User user) {
        CarteFidelite carte = carteRepository.findByUser(user).orElse(null);

        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .isActive(user.isActive())
                .loyaltyLevel(carte != null ? carte.getLevel().name() : "SILVER")
                .loyaltyPoints(carte != null ? carte.getPoints() : 0)
                .build();
    }

    // =========================
// 📧 SEND VERIFICATION EMAIL
// =========================
    private void sendVerificationEmail(User user) {

        // supprimer ancien token si existe
        verificationTokenRepository.deleteByUser(user);

        // générer token
        String token = UUID.randomUUID().toString();

        // sauvegarder token
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        // envoyer email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
}