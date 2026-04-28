package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-mail")
@RequiredArgsConstructor
@Slf4j
public class TestMailController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<String> testMail(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Utilisateur non authentifie");
        }

        log.info("Utilisateur trouve: id={}, nom={}, email={}", user.getId(), user.getNom(), user.getEmail());
        log.info("Envoi mail test vers {}", user.getEmail());

        emailService.sendWarningEmail(
                user.getEmail(),
                user.getNom(),
                "Test commentaire depuis l'utilisateur connecte"
        );

        return ResponseEntity.ok("Mail test envoye a : " + user.getEmail());
    }
}
