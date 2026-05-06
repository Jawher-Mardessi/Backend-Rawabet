package org.example.rawabet.dto.feedback.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackModerationResponse {
    private boolean success;
    private boolean containsBadWords;
    private String message;
    private FeedbackResponse feedback;
}
