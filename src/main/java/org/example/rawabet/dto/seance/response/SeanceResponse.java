package org.example.rawabet.dto.seance.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeanceResponse {
    private Long id;
    private String dateHeure;
    private double prixBase;
    private String langue;
    private Long filmId;
    private Long salleCinemaId;
}