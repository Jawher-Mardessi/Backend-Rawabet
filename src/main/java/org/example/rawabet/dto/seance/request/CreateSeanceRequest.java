package org.example.rawabet.dto.seance.request;

import lombok.Data;

@Data
public class CreateSeanceRequest {
    private String dateHeure;
    private double prixBase;
    private String langue;
    private Long filmId;
    private Long salleCinemaId;
}