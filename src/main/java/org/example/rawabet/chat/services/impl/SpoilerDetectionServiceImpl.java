package org.example.rawabet.chat.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.chat.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class SpoilerDetectionServiceImpl {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String FLASK_URL = "http://localhost:5000/detect";

    public boolean isSpoiler(String messageContent, Long messageId) {
        try {
            String body = objectMapper.writeValueAsString(
                    java.util.Map.of("text", messageContent)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FLASK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            JsonNode json = objectMapper.readTree(response.body());
            boolean spoiler = json.path("isSpoiler").asBoolean(false);

            // ✅ Sauvegarder en BDD une seule fois
            if (spoiler && messageId != null) {
                messageRepository.findById(messageId).ifPresent(message -> {
                    message.setSpoiler(true);
                    messageRepository.save(message);
                });
            }

            return spoiler;

        } catch (Exception e) {
            System.err.println("[SpoilerDetection] Erreur : " + e.getMessage());
            return false;
        }
    }
}