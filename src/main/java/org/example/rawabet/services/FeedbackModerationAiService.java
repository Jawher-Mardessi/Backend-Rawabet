package org.example.rawabet.services;

import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.dto.feedback.ai.FeedbackModerationAiRequest;
import org.example.rawabet.dto.feedback.ai.FeedbackModerationAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FeedbackModerationAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.feedback.badwords-ai-url:http://localhost:8000/feedback/moderation}")
    private String moderationApiUrl;

    public FeedbackModerationAiResponse analyze(String commentaire) {
        if (commentaire == null || commentaire.isBlank()) {
            return FeedbackModerationAiResponse.builder()
                    .hasBadWords(false)
                    .score(0.0)
                    .severity("clean")
                    .model("feedback-ai-fallback")
                    .build();
        }

        try {
            ResponseEntity<FeedbackModerationAiResponse> response = restTemplate.postForEntity(
                    moderationApiUrl,
                    FeedbackModerationAiRequest.builder().commentaire(commentaire).build(),
                    FeedbackModerationAiResponse.class
            );
            FeedbackModerationAiResponse payload = response.getBody();
            if (payload == null) {
                return FeedbackModerationAiResponse.builder()
                        .hasBadWords(false)
                        .score(0.0)
                        .severity("clean")
                        .model("feedback-ai-null-response")
                        .build();
            }
            return payload;
        } catch (Exception exception) {
            log.error("Erreur appel moderation model: {}", moderationApiUrl, exception);
            return FeedbackModerationAiResponse.builder()
                    .hasBadWords(false)
                    .score(0.0)
                    .severity("clean")
                    .model("feedback-ai-error")
                    .build();
        }
    }
}
