package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UserResponse;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.Level;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        User savedUser = userRepository.save(user);

        // 💳 création carte
        carteRepository.save(createCarte(savedUser));

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

        // 🔐 sécurité
        if (roleNames.contains("SUPER_ADMIN")) {
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        }

        List<Role> roles = roleRepository.findByNameIn(roleNames);

        // 🔥 validation stricte
        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("Some roles are invalid");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // 💳 création carte
        carteRepository.save(createCarte(savedUser));

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
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .toList()
                )
                .build();
    }
}