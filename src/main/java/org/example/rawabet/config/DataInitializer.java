package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.*;
import org.example.rawabet.enums.Level;
import org.example.rawabet.repositories.*;
import org.example.rawabet.entities.Permission;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.PermissionRepository;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.AbonnementServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PermissionRepository       permissionRepository;
    private final RoleRepository             roleRepository;
    private final UserRepository             userRepository;
    private final CarteFideliteRepository    carteRepository;
    private final BCryptPasswordEncoder      passwordEncoder;
    private final AbonnementServiceImpl abonnementService;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            abonnementService.initAbonnements();

            // ✅ Toujours s'assurer que ADMIN_MANAGE existe
            Permission adminManage = createPermission("ADMIN", "MANAGE");

            // ✅ Toujours s'assurer que le SuperAdmin a ADMIN_MANAGE
            roleRepository.findByName("SUPER_ADMIN").ifPresent(superAdmin -> {
                boolean hasAdminManage = superAdmin.getPermissions().stream()
                        .anyMatch(p -> p.getName().equals("ADMIN_MANAGE"));
                if (!hasAdminManage) {
                    superAdmin.getPermissions().add(adminManage);
                    roleRepository.save(superAdmin);
                    System.out.println("✅ ADMIN_MANAGE added to SUPER_ADMIN");
                }
            });

            // ✅ Ajouter CLIENT + carte fidélité à tous les admins existants
            ensureAdminsHaveClientRoleAndCarte();

            // ✅ Si déjà initialisé → skip
            if (roleRepository.count() > 1) {
                System.out.println("✅ RBAC already initialized — skipping full init");
                return;
            }

            // 🔐 PERMISSIONS
            createPermission("CINEMA", "CREATE");
            createPermission("CINEMA", "READ");
            createPermission("CINEMA", "UPDATE");
            createPermission("CINEMA", "DELETE");

            createPermission("FILM", "CREATE");
            createPermission("FILM", "READ");
            createPermission("FILM", "UPDATE");
            createPermission("FILM", "DELETE");

            createPermission("EVENT", "CREATE");
            createPermission("EVENT", "READ");
            createPermission("EVENT", "UPDATE");
            createPermission("EVENT", "DELETE");

            createPermission("CLUB", "CREATE");
            createPermission("CLUB", "READ");
            createPermission("CLUB", "UPDATE");
            createPermission("CLUB", "DELETE");
            createPermission("CLUB", "MANAGE");

            createPermission("FIDELITY", "READ");
            createPermission("FIDELITY", "UPDATE");

            // 🎭 ROLES
            Role superAdmin  = createRole("SUPER_ADMIN");
            Role adminCinema = createRole("ADMIN_CINEMA");
            Role adminEvent  = createRole("ADMIN_EVENT");
            Role adminClub   = createRole("ADMIN_CLUB");
            Role client      = createRole("CLIENT");

            // 🔗 PERMISSIONS
            adminCinema.setPermissions(permissionRepository.findByModule("CINEMA"));
            adminEvent.setPermissions(permissionRepository.findByModule("EVENT"));
            adminClub.setPermissions(permissionRepository.findByModule("CLUB"));
            client.setPermissions(permissionRepository.findByModule("FIDELITY"));

            HashSet<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            superAdmin.setPermissions(new ArrayList<>(allPermissions));

            roleRepository.saveAll(List.of(superAdmin, adminCinema, adminEvent, adminClub, client));

            createSuperAdmin();

            System.out.println("🔥 RBAC initialized successfully");
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // À chaque démarrage : ajoute CLIENT + carte fidélité aux admins existants
    // Gère aussi les admins créés avant ce fix.
    // ════════════════════════════════════════════════════════════════════════
    private void ensureAdminsHaveClientRoleAndCarte() {
        roleRepository.findByName("CLIENT").ifPresent(clientRole -> {
            List<String> adminRoleNames = List.of(
                    "SUPER_ADMIN", "ADMIN_CINEMA", "ADMIN_EVENT", "ADMIN_CLUB");

            userRepository.findAll().forEach(user -> {
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(r -> adminRoleNames.contains(r.getName()));
                if (!isAdmin) return;

                // 1. Ajouter CLIENT si manquant
                boolean hasClient = user.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("CLIENT"));
                if (!hasClient) {
                    List<Role> roles = new ArrayList<>(user.getRoles());
                    roles.add(clientRole);
                    user.setRoles(roles);
                    userRepository.save(user);
                    System.out.println("✅ CLIENT ajouté à : " + user.getEmail());
                }

                // 2. Créer carte fidélité si manquante
                if (carteRepository.findByUser(user).isEmpty()) {
                    carteRepository.save(buildCarte(user));
                    System.out.println("✅ Carte fidélité créée pour : " + user.getEmail());
                }
            });
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private Permission createPermission(String module, String action) {
        String name = module + "_" + action;
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setModule(module);
                    p.setAction(action);
                    p.setName(name);
                    return permissionRepository.save(p);
                });
    }

    private Role createRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(name);
                    return roleRepository.save(r);
                });
    }

    private void createSuperAdmin() {
        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
            Role clientRole     = roleRepository.findByName("CLIENT").orElseThrow();

            User admin = new User();
            admin.setNom("SuperAdmin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRoles(List.of(superAdminRole, clientRole));
            admin.setActive(true);

            User saved = userRepository.save(admin);
            carteRepository.save(buildCarte(saved));
            System.out.println("🔥 SUPER_ADMIN created with CLIENT role + carte fidélité");
        }
    }

    private CarteFidelite buildCarte(User user) {
        return CarteFidelite.builder()
                .user(user)
                .points(0)
                .level(Level.SILVER)
                .dateExpiration(LocalDate.now().plusYears(1))
                .build();
    }
}