package org.example.rawabet.chat.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class RawaBotService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String ask(String filmName, String question) {
        try {
            String prompt = String.format(
                    "Tu es Cinéphile, un assistant expert en cinéma intégré dans l'application Rawebet. " +
                            "Tu réponds aux questions des spectateurs pendant la diffusion du film \"%s\". " +
                            "Réponds en français, de façon concise (3-4 phrases max), avec enthousiasme et expertise cinématographique. " +
                            "Question : %s",
                    filmName, question
            );

            String body = objectMapper.writeValueAsString(new java.util.LinkedHashMap<>() {{
                put("model", "claude-haiku-4-5-20251001");
                put("max_tokens", 512);
                put("messages", java.util.List.of(
                        java.util.Map.of("role", "user", "content", prompt)
                ));
            }});

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = objectMapper.readTree(response.body());
            return json.path("content").get(0).path("text").asText("Je n'ai pas pu répondre à cette question.");

        } catch (Exception e) {
            System.err.println("[RawaBot] Erreur : " + e.getMessage());
            return "Désolé, je ne suis pas disponible pour le moment. 🎬";
        }
    }
}