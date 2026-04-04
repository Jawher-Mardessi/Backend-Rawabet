package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.RegisterRequest;
import org.example.rawabet.dto.UserResponse;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================
    // ✅ CREATE USER (CLIENT)
    // =========================
    @Override
    public UserResponse addUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(clientRole));

        return mapToResponse(userRepository.save(user));
    }

    // =========================
    // 🔐 CREATE USER WITH ROLE
    // =========================
    @Override
    public UserResponse addUserWithRole(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new RuntimeException("Roles are required");
        }

        // 🔐 BLOCK SUPER_ADMIN
        // 🔐 NORMALIZE ROLES (IMPORTANT)
        List<String> roleNames = request.getRoles()
                .stream()
                .map(String::toUpperCase)
                .toList();

// 🔐 BLOCK SUPER_ADMIN (SECURE)
        if (roleNames.contains("SUPER_ADMIN")) {
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        }

// 🔐 FETCH ROLES
        List<Role> roles = roleRepository.findByNameIn(roleNames);

        if (roles.isEmpty()) {
            throw new RuntimeException("No valid roles found");
        }
        if (roles.isEmpty()) {
            throw new RuntimeException("No valid roles found");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        return mapToResponse(userRepository.save(user));
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
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
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
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

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