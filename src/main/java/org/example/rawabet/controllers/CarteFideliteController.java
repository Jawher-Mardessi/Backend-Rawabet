package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.CarteFideliteResponse;
import org.example.rawabet.services.ICarteFideliteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carte")
@RequiredArgsConstructor
public class CarteFideliteController {

    private final ICarteFideliteService carteService;

    // 🔐 voir sa carte
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FIDELITY_READ')")
    public CarteFideliteResponse getMyCarte() {
        return carteService.getMyCarte();
    }
}