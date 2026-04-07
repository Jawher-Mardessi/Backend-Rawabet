package org.example.rawabet.dto.feedback.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private int note;
    private String commentaire;
    private String date;
    private Long userId;
    private Long filmId;
}