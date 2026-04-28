package org.example.rawabet.dto.feedback.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackModerationAiMatch {
    private String word;
    private int occurrences;
}
