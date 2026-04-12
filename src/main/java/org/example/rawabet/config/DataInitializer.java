package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Permission;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.PermissionRepository;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // ✅ Toujours s'assurer que ADMIN_MANAGE existe (même si RBAC déjà initialisé)
            Permission adminManage = createPermission("ADMIN", "MANAGE");

            // ✅ Toujours s'assurer que le SuperAdmin a ADMIN_MANAGE
            roleRepository.findByName("SUPER_ADMIN").ifPresent(superAdmin -> {
                boolean hasAdminManage = superAdmin.getPermissions().stream()
                        .anyMatch(p -> p.getName().equals("ADMIN_MANAGE"));
                if (!hasAdminManage) {
                    superAdmin.getPermissions().add(adminManage);
                    roleRepository.save(superAdmin);
                    System.out.println("✅ ADMIN_MANAGE permission added to SUPER_ADMIN");
                }
            });

            // ✅ Si déjà initialisé → skip la création des rôles/permissions
            if (roleRepository.count() > 1) {
                System.out.println("✅ RBAC already initialized — skipping full init");
                return;
            }

            // 🔐 PERMISSIONS METIER
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
            Role superAdmin = createRole("SUPER_ADMIN");
            Role adminCinema = createRole("ADMIN_CINEMA");
            Role adminEvent = createRole("ADMIN_EVENT");
            Role adminClub = createRole("ADMIN_CLUB");
            Role client = createRole("CLIENT");

            // 🔗 ASSIGN PERMISSIONS
            adminCinema.setPermissions(permissionRepository.findByModule("CINEMA"));
            adminEvent.setPermissions(permissionRepository.findByModule("EVENT"));
            adminClub.setPermissions(permissionRepository.findByModule("CLUB"));

            client.setPermissions(permissionRepository.findByModule("FIDELITY"));

            HashSet<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            superAdmin.setPermissions(new ArrayList<>(allPermissions));

            roleRepository.saveAll(List.of(
                    superAdmin,
                    adminCinema,
                    adminEvent,
                    adminClub,
                    client
            ));

            createSuperAdmin();

            System.out.println("🔥 RBAC initialized successfully");
        };
    }

    private Permission createPermission(String module, String action) {
        String name = module + "_" + action;

        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setModule(module);
                    permission.setAction(action);
                    permission.setName(name);
                    return permissionRepository.save(permission);
                });
    }

    private Role createRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }

    private void createSuperAdmin() {

        if (userRepository.findByEmail("admin@test.com").isEmpty()) {

            Role role = roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow();

            User admin = new User();
            admin.setNom("SuperAdmin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRoles(List.of(role));
            admin.setActive(true); // ✅ Fix — SuperAdmin doit être actif

            userRepository.save(admin);

            System.out.println("🔥 SUPER_ADMIN created");
        }
    }
}