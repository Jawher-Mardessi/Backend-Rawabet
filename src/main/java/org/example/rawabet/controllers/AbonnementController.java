package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.services.AbonnementServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementServiceImpl abonnementService;

    // 🔹 GET ALL PACKS
    @GetMapping
    public List<Abonnement> getAll() {
        return abonnementService.getAllAbonnements();
    }

    // 🔹 SUBSCRIBE
    @PostMapping("/subscribe")
    public UserAbonnement subscribe(
            @RequestParam Long userId,
            @RequestParam Long abonnementId
    ) {
        return abonnementService.subscribe(userId, abonnementId);
    }

    // 🔹 GET ALL USER ABONNEMENTS
    @GetMapping("/users")
    public List<UserAbonnement> getUserAbonnements() {
        return abonnementService.getUserAbonnements();
    }

    @DeleteMapping("/users/expired")
    public long cleanupExpiredUserAbonnements() {
        return abonnementService.cleanupExpiredUserAbonnements();
    }
}
