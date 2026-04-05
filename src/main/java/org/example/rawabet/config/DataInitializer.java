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

import java.util.HashSet;
import java.util.Set;

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

            createPermission("FORMATION", "CREATE");
            createPermission("FORMATION", "READ");
            createPermission("FORMATION", "UPDATE");
            createPermission("FORMATION", "DELETE");

            // 🔥 SYSTEM PERMISSION
            Permission adminManage = createPermission("ADMIN", "MANAGE");

            // 🎭 ROLES
            Role superAdmin = createRole("SUPER_ADMIN");
            Role adminCinema = createRole("ADMIN_CINEMA");
            Role adminEvent = createRole("ADMIN_EVENT");
            Role adminFormation = createRole("ADMIN_FORMATION");
            Role client = createRole("CLIENT");

            // 🔗 ASSIGN PERMISSIONS

            // ✅ FIX : ADMIN_CINEMA reçoit CINEMA_* + FILM_* et est bien sauvegardé
            Set<Permission> cinemaPermissions = new HashSet<>(permissionRepository.findByModule("CINEMA"));
            cinemaPermissions.addAll(permissionRepository.findByModule("FILM"));
            adminCinema.setPermissions(cinemaPermissions);
            roleRepository.save(adminCinema);

            // ✅ FIX : adminEvent sauvegardé
            adminEvent.setPermissions(new HashSet<>(permissionRepository.findByModule("EVENT")));
            roleRepository.save(adminEvent);

            // ✅ FIX : adminFormation sauvegardé
            adminFormation.setPermissions(new HashSet<>(permissionRepository.findByModule("FORMATION")));
            roleRepository.save(adminFormation);

            // CLIENT : aucune permission spéciale
            roleRepository.save(client);

            // SUPER_ADMIN : toutes les permissions
            HashSet<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            allPermissions.add(adminManage);
            superAdmin.setPermissions(allPermissions);
            roleRepository.save(superAdmin);

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
            admin.setRoles(new HashSet<>(Set.of(role)));

            userRepository.save(admin);

            System.out.println("🔥 SUPER_ADMIN created");
        }
    }
}