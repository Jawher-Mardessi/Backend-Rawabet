package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.AdminActivityEvent;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceAuthService {

    @Value("${face.api.url:http://localhost:5000}")
    private String faceApiUrl;

    private final RestTemplate           restTemplate;
    private final UserRepository         userRepository;
    private final JwtService             jwtService;
    private final AdminActivityPublisher activityPublisher; // ← AJOUT

    // ─────────────────────────────────────────
    //  REGISTER — enregistre le visage dans Flask
    // ─────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public String registerFace(String email, String imageBase64) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("email", email, "image", imageBase64);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    faceApiUrl + "/register", entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Réponse vide du serveur de reconnaissance faciale");
            }

            String status = (String) responseBody.get("status");
            String message = (String) responseBody.getOrDefault("message", "Opération effectuée");

            if (!"ok".equals(status)) {
                throw new RuntimeException(message);
            }

            log.info("[FaceAuth] Visage enregistré pour : {}", email);
            return message;

        } catch (RestClientException e) {
            log.error("[FaceAuth] Service Flask indisponible lors du register : {}", e.getMessage());
            throw new RuntimeException("Le service de reconnaissance faciale est temporairement indisponible");
        }
    }

    // ─────────────────────────────────────────
    //  STATUS — vérifie si un visage est enregistré
    // ─────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public boolean hasFaceRegistered(String email) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    faceApiUrl + "/status?email=" + email, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null) return false;
            return Boolean.TRUE.equals(body.get("exists"));
        } catch (Exception e) {
            log.warn("[FaceAuth] Impossible de vérifier le statut visage pour {} : {}", email, e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────
    //  LOGIN — vérifie le visage et retourne un JWT Spring
    // ─────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public AuthResponse loginWithFace(String imageBase64) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("image", imageBase64);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> flaskResponse;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    faceApiUrl + "/login-face", entity, Map.class);
            flaskResponse = response.getBody();
        } catch (RestClientException e) {
            log.error("[FaceAuth] Service Flask indisponible lors du login : {}", e.getMessage());
            throw new RuntimeException("Le service de reconnaissance faciale est temporairement indisponible");
        }

        if (flaskResponse == null) {
            throw new RuntimeException("Réponse vide du serveur de reconnaissance faciale");
        }

        Boolean match = (Boolean) flaskResponse.get("match");
        if (!Boolean.TRUE.equals(match)) {
            String message = (String) flaskResponse.getOrDefault("message", "Visage non reconnu");
            throw new RuntimeException(message);
        }

        String email = (String) flaskResponse.get("user");
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email non trouvé dans la réponse du service facial");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));

        if (!user.isActive()) {
            throw new RuntimeException("Ce compte a été désactivé");
        }
        if (user.isCurrentlyBanned()) {
            throw new RuntimeException("Ce compte est actuellement suspendu");
        }

        String jwt = jwtService.generateToken(user);
        activityPublisher.publish(AdminActivityEvent.faceLogin(email)); // ← AJOUT
        log.info("[FaceAuth] Login par visage réussi pour : {}", email);

        return new AuthResponse(jwt, user.getId());
    }
}