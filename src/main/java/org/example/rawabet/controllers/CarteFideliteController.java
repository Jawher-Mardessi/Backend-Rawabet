package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.CarteFideliteResponse;
import org.example.rawabet.dto.FidelityHistoryResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.ICarteFideliteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carte")
@RequiredArgsConstructor
public class CarteFideliteController {

    private final ICarteFideliteService carteService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public CarteFideliteResponse getMyCarte() {
        return carteService.getMyCarte();
    }

    @PostMapping("/admin/add-points/{userId}")
    @PreAuthorize("hasAuthority('FIDELITY_UPDATE')")
    public CarteFideliteResponse addPointsByAdmin(
            @PathVariable Long userId,
            @RequestParam int points) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        carteService.addPointsByAdmin(user, points, ActionType.BONUS);

        return carteService.getCarteByUser(user);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public List<FidelityHistoryResponse> getMyHistory() { // ✅ DTO
        return carteService.getMyHistory();
    }
}