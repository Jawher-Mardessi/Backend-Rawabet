package org.example.rawabet.dto.feedback.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackModerationAiResponse {
    private boolean hasBadWords;
    private double score;
    private String severity;
    private List<FeedbackModerationAiMatch> matches;
    private String model;
}
