package org.example.rawabet.dto.feedback.request;

import lombok.Data;

@Data
public class CreateFeedbackRequest {
    private Long userId;
    private Long filmId;
    private String commentaire;
    private int note;
}