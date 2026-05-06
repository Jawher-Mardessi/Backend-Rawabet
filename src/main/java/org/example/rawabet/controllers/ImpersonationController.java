package org.example.rawabet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.ImpersonateRequest;
import org.example.rawabet.services.IAuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ImpersonationController {

    private final IAuthService authService;

    /**
     * POST /auth/impersonate
     *
     * Accessible uniquement aux utilisateurs avec un rôle admin.
     * Génère un token CLIENT pour le targetUserId.
     *
     * Corps JSON : { "targetUserId": 42 }
     */
    @PostMapping("/impersonate")
    @PreAuthorize("hasAnyAuthority('ADMIN_MANAGE','CINEMA_CREATE','EVENT_CREATE','CLUB_MANAGE','CLUB_CREATE')")
    public AuthResponse impersonate(@Valid @RequestBody ImpersonateRequest request) {
        // ✅ FIX — @Data Lombok génère getTargetUserId(), pas targetUserId()
        return authService.impersonate(request.getTargetUserId());
    }
}
