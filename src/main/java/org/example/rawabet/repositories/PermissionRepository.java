package org.example.rawabet.repositories;

import org.example.rawabet.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // 🔐 récupérer permissions par module (CINEMA, EVENT...)
    List<Permission> findByModule(String module);

    // 🔍 récupérer une permission par nom (CINEMA_CREATE)
    Optional<Permission> findByName(String name);

    // 🚀 VERSION PRO → récupérer plusieurs permissions en une requête
    List<Permission> findByNameIn(List<String> names);
}