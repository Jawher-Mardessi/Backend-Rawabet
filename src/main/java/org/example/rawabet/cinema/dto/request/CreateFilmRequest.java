package org.example.rawabet.cinema.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateFilmRequest {

    @NotBlank(message = "Le titre du film est obligatoire")
    private String title;

    private String synopsis;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être supérieure à 0")
    private Integer durationMinutes;

    private String language;

    private String genre;

    private String director;

    private String castSummary;

    private String rating;

    private LocalDate releaseDate;

    private String posterUrl;

    private String trailerUrl;

    private String imdbId;

    // ── Champs pour la prédiction ROI ────────────────────────────
    private Double budget;

    private Double popularity;
}