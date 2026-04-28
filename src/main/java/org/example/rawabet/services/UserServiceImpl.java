package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.*;
import org.example.rawabet.entities.*;
import org.example.rawabet.enums.Level;
import org.example.rawabet.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;

    private final UserRepository                    userRepository;
    private final RoleRepository                    roleRepository;
    private final PasswordEncoder                   passwordEncoder;
    private final CarteFideliteRepository           carteRepository;
    private final IAuthService                      authService;
    private final EmailVerificationTokenRepository  verificationTokenRepository;
    private final EmailService                      emailService;
    private final AdminActivityPublisher            activityPublisher; // ← AJOUT

    // ── Register (CLIENT) ─────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        checkEmail(request.getEmail());
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT role not found"));
        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(clientRole));
        user.setActive(true);
        try {
            User saved = userRepository.save(user);
            carteRepository.save(createCarte(saved));
            activityPublisher.publish(AdminActivityEvent.userRegister(saved.getEmail())); // ← AJOUT
            return mapToResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }
    }

    // ── Create by admin ───────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse createUserByAdmin(RegisterRequest request) {
        checkEmail(request.getEmail());
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new RuntimeException("Roles are required");
        }
        List<String> roleNames = request.getRoles().stream()
                .map(String::toUpperCase)
                .toList();
        if (roleNames.contains("SUPER_ADMIN")) {
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        }
        List<Role> roles = new ArrayList<>(roleRepository.findByNameIn(roleNames));
        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("Some roles are invalid");
        }
        boolean alreadyHasClient = roles.stream()
                .anyMatch(r -> r.getName().equals("CLIENT"));
        if (!alreadyHasClient) {
            Role clientRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(() -> new RuntimeException("CLIENT role not found"));
            roles.add(clientRole);
        }
        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        user.setActive(true);
        try {
            User saved = userRepository.save(user);
            carteRepository.save(createCarte(saved));
            return mapToResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }
    }

    // ── Update user (admin) ───────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNom(request.getNom());
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail().toLowerCase().trim());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setTokenVersion(user.getTokenVersion() + 1);
        }
        try {
            return mapToResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        carteRepository.findByUser(user).ifPresent(carteRepository::delete);
        userRepository.delete(user);
    }

    // ── Get by id ─────────────────────────────────────────────────────────
    @Override
    public UserResponse getUserById(Long id) {
        return mapToResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    // ── Get all paged ─────────────────────────────────────────────────────
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    // ── My profile ────────────────────────────────────────────────────────
    @Override
    public UserResponse getMyProfile() {
        return mapToResponse(authService.getAuthenticatedUser());
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        User user = authService.getAuthenticatedUser();
        user.setNom(request.getNom());
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail().toLowerCase().trim());
        }
        try {
            return mapToResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }
    }

    @Override
    @Transactional
    public UserResponse uploadMyAvatar(MultipartFile file) {
        User user = authService.getAuthenticatedUser();
        if (file == null || file.isEmpty())
            throw new RuntimeException("Aucun fichier image fourni");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new RuntimeException("Format invalide — envoyez une image");
        if (file.getSize() > MAX_AVATAR_SIZE)
            throw new RuntimeException("Image trop volumineuse (max 5MB)");
        validateImageMagicBytes(file);
        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String filename = "avatar-" + user.getId() + "-" + UUID.randomUUID() + extension;
        Path avatarDir = Paths.get("uploads", "avatars").toAbsolutePath().normalize();
        Path target    = avatarDir.resolve(filename);
        try {
            Files.createDirectories(avatarDir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'enregistrer l'image", e);
        }
        String oldUrl = user.getAvatarUrl();
        user.setAvatarUrl("/uploads/avatars/" + filename);
        UserResponse response = mapToResponse(userRepository.save(user));
        deleteOldAvatarFile(oldUrl);
        return response;
    }

    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new RuntimeException("Ancien mot de passe incorrect");
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new RuntimeException("Le nouveau mot de passe doit être différent");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    // ── Update roles ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse updateUserRoles(Long id, UpdateUserRolesRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getName()));
        if (isSuperAdmin)
            throw new RuntimeException("Impossible de modifier les rôles d'un SUPER_ADMIN");
        List<String> roleNames = request.getRoles().stream()
                .map(String::toUpperCase).distinct().toList();
        if (roleNames.contains("SUPER_ADMIN"))
            throw new RuntimeException("Cannot assign SUPER_ADMIN role");
        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size())
            throw new RuntimeException("Some roles are invalid");
        user.setRoles(roles);
        user.setTokenVersion(user.getTokenVersion() + 1);
        return mapToResponse(userRepository.save(user));
    }

    // ── BAN TEMPORAIRE ────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse banUser(Long id, BanRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive() && (user.getBanUntil() == null
                || LocalDateTime.now().isBefore(user.getBanUntil()))) {
            throw new RuntimeException("User déjà banni");
        }
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getName()));
        if (isSuperAdmin)
            throw new RuntimeException("Impossible de bannir un SUPER_ADMIN");
        if (request.getBanUntil() != null
                && request.getBanUntil().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date de fin de ban doit être dans le futur");
        }
        user.setActive(false);
        user.setBanUntil(request.getBanUntil());
        user.setBanReason(request.getReason());
        user.setTokenVersion(user.getTokenVersion() + 1);
        User saved = userRepository.save(user);
        emailService.sendBanNotification(
                user.getEmail(),
                user.getNom(),
                request.getBanUntil(),
                request.getReason()
        );
        activityPublisher.publish(AdminActivityEvent.userBan(user.getEmail())); // ← AJOUT
        return mapToResponse(saved);
    }

    // ── UNBAN ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponse unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isActive())
            throw new RuntimeException("User déjà actif");
        user.setActive(true);
        user.setBanUntil(null);
        user.setBanReason(null);
        user.setTokenVersion(user.getTokenVersion() + 1);
        UserResponse saved = mapToResponse(userRepository.save(user));
        activityPublisher.publish(AdminActivityEvent.userUnban(user.getEmail())); // ← AJOUT
        return saved;
    }

    // ── Private helpers ───────────────────────────────────────────────────
    private void checkEmail(String email) {
        if (userRepository.findByEmail(email.toLowerCase().trim()).isPresent())
            throw new RuntimeException("Email already exists");
    }

    private CarteFidelite createCarte(User user) {
        return CarteFidelite.builder()
                .user(user)
                .points(0)
                .level(Level.SILVER)
                .dateExpiration(LocalDate.now().plusYears(1))
                .build();
    }

    private UserResponse mapToResponse(User user) {
        CarteFidelite carte = carteRepository.findByUser(user).orElse(null);
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .isActive(user.isActive())
                .loyaltyLevel(carte != null ? carte.getLevel().name() : "SILVER")
                .loyaltyPoints(carte != null ? carte.getPoints() : 0)
                .createdAt(user.getCreatedAt())
                .banUntil(user.getBanUntil())
                .banReason(user.getBanReason())
                .loginFailedAttempts(user.getLoginFailedAttempts())
                .loginLockedUntil(user.getLoginLockedUntil())
                .build();
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            if (ext.matches("\\.(png|jpg|jpeg|webp|gif)")) return ext;
        }
        if ("image/png".equalsIgnoreCase(contentType))  return ".png";
        if ("image/jpeg".equalsIgnoreCase(contentType)) return ".jpg";
        if ("image/webp".equalsIgnoreCase(contentType)) return ".webp";
        if ("image/gif".equalsIgnoreCase(contentType))  return ".gif";
        return ".img";
    }

    private void deleteOldAvatarFile(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.startsWith("/uploads/avatars/")) return;
        String filename = avatarUrl.substring("/uploads/avatars/".length());
        Path file = Paths.get("uploads", "avatars", filename).toAbsolutePath().normalize();
        try { Files.deleteIfExists(file); } catch (IOException ignored) {}
    }

    private void validateImageMagicBytes(MultipartFile file) {
        try {
            byte[] h = file.getInputStream().readNBytes(12);
            if (!isPng(h) && !isJpeg(h) && !isGif(h) && !isWebp(h))
                throw new RuntimeException("Contenu du fichier invalide — image PNG, JPEG, WEBP ou GIF requise");
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire le fichier");
        }
    }

    private boolean isPng(byte[] h)  { return h.length >= 4  && h[0]==(byte)0x89 && h[1]==0x50 && h[2]==0x4E && h[3]==0x47; }
    private boolean isJpeg(byte[] h) { return h.length >= 3  && h[0]==(byte)0xFF && h[1]==(byte)0xD8 && h[2]==(byte)0xFF; }
    private boolean isGif(byte[] h)  { return h.length >= 4  && h[0]==0x47 && h[1]==0x49 && h[2]==0x46 && h[3]==0x38; }
    private boolean isWebp(byte[] h) { return h.length >= 12 && h[0]==0x52 && h[1]==0x49 && h[2]==0x46 && h[3]==0x46
            && h[8]==0x57 && h[9]==0x45 && h[10]==0x42 && h[11]==0x50; }

    private void sendVerificationEmail(User user) {
        verificationTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        EmailVerificationToken vt = new EmailVerificationToken();
        vt.setToken(token);
        vt.setUser(user);
        vt.setExpiresAt(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(vt);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
}