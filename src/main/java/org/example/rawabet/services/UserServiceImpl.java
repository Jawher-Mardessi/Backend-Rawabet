package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Role;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.RoleRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    // CREATE
    @Override
    public User addUser(User user) {

        // 🔐 encoder password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 🎯 récupérer rôle CLIENT par défaut
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 🔥 affecter rôle automatiquement
        user.setRoles(List.of(role));

        return userRepository.save(user);
    }
    // ✅ CREATE avec rôle spécifique (pour SUPER_ADMIN)
    @Override
    public User addUserWithRole(User user, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }

    // ✅ UPDATE → ne jamais écraser le password
    @Override
    public User updateUser(User user) {
        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setNom(user.getNom());
        existing.setEmail(user.getEmail());
        // ⚠️ password non modifié ici → faire un endpoint séparé pour ça
        return userRepository.save(existing);
    }

    // DELETE
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // GET BY ID
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // GET ALL
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}