package org.example.rawabet.cinema.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "films")

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor

public class Film extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String synopsis;

    private Integer durationMinutes;

    private String language;

    private String genre;

    private String director;

    private String castSummary;

    private String rating;

    private LocalDate releaseDate;

    private String posterUrl;

    private String trailerUrl;

    @Column(unique = true)
    private String imdbId;

    @Builder.Default
    private Float averageRating = 0f;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private Boolean isActive = true;

    // ── Prédiction ROI ───────────────────────────────────────────
    private Boolean profitable;

    private Double roiConfidence;

    private String roiLabel;
}