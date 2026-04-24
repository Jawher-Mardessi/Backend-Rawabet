package org.example.rawabet.cinema.services.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FilmRoiService {

    private static final String FASTAPI_URL = "http://localhost:8000/predict";

    private final RestTemplate restTemplate = new RestTemplate();

    public RoiPredictionResult predict(String title, double budget, double runtime,
                                       int releaseYear, int releaseMonth,
                                       String releaseDate, String language,
                                       List<String> genres, String overview) {

        RoiRequest request = new RoiRequest(title, budget, runtime,
                releaseYear, releaseMonth,
                releaseDate, language, genres, overview);
        try {
            ResponseEntity<RoiPredictionResult> response = restTemplate.postForEntity(
                    FASTAPI_URL, request, RoiPredictionResult.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // ── DTOs ─────────────────────────────────────────────────────

    @Data
    public static class RoiRequest {
        private String title;
        private double budget;
        private double runtime;
        @JsonProperty("release_year")
        private int releaseYear;
        @JsonProperty("release_month")
        private int releaseMonth;
        @JsonProperty("release_date")
        private String releaseDate;
        private String language;
        private List<String> genres;
        private String overview;

        public RoiRequest(String title, double budget, double runtime,
                          int releaseYear, int releaseMonth, String releaseDate,
                          String language, List<String> genres, String overview) {
            this.title        = title;
            this.budget       = budget;
            this.runtime      = runtime;
            this.releaseYear  = releaseYear;
            this.releaseMonth = releaseMonth;
            this.releaseDate  = releaseDate;
            this.language     = language;
            this.genres       = genres;
            this.overview     = overview != null ? overview : "";
        }
    }

    @Data
    public static class RoiPredictionResult {
        private String title;
        @JsonProperty("ai_score")
        private double aiScore;
        @JsonProperty("temporal_score")
        private double temporalScore;
        @JsonProperty("final_score")
        private double finalScore;
        @JsonProperty("temporal_label")
        private String temporalLabel;
        @JsonProperty("temporal_status")
        private String temporalStatus;
        @JsonProperty("weeks_since_release")
        private Double weeksSinceRelease;
        private String recommendation;
        @JsonProperty("recommendation_level")
        private String recommendationLevel;
        private String label;
    }
}