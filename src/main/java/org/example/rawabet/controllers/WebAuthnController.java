package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.webauthn.PasskeyFinishAuthenticationRequest;
import org.example.rawabet.dto.webauthn.PasskeyFinishRegistrationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartAuthenticationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartAuthenticationResponse;
import org.example.rawabet.dto.webauthn.PasskeyStartRegistrationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartRegistrationResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.PasskeyCredentialRepository;
import org.example.rawabet.services.IAuthService;
import org.example.rawabet.services.PasskeyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/webauthn")
@RequiredArgsConstructor
public class WebAuthnController {

    private final PasskeyService passkeyService;
    private final IAuthService authService;
    private final PasskeyCredentialRepository passkeyCredentialRepository;

    @PostMapping("/registration/start")
    public PasskeyStartRegistrationResponse startRegistration(@RequestBody PasskeyStartRegistrationRequest request) {
        return passkeyService.startRegistration(request);
    }

    @PostMapping("/registration/finish")
    public void finishRegistration(@RequestBody PasskeyFinishRegistrationRequest request) {
        passkeyService.finishRegistration(request);
    }

    @PostMapping("/authentication/start")
    public PasskeyStartAuthenticationResponse startAuthentication(@RequestBody(required = false) PasskeyStartAuthenticationRequest request) {
        return passkeyService.startAuthentication(request);
    }

    @PostMapping("/authentication/finish")
    public AuthResponse finishAuthentication(@RequestBody PasskeyFinishAuthenticationRequest request) {
        return passkeyService.finishAuthentication(request);
    }

    // AJOUT — vérifie si l'utilisateur connecté a déjà des passkeys enregistrées
    // Utilisé par le frontend pour afficher "Modifier" au lieu de "Activer"
    @GetMapping("/my-credentials")
    public Map<String, Object> getMyCredentials() {
        User user = authService.getAuthenticatedUser();
        String userHandle = String.valueOf(user.getId());
        long count = passkeyCredentialRepository.findByUserHandle(userHandle).size();
        return Map.of(
                "hasPasskey", count > 0,
                "count", count
        );
    }
}