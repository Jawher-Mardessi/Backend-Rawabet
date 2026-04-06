package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.ActionType;
import java.time.Instant;

@Data
@Builder
public class FidelityHistoryResponse {
    private ActionType action;
    private int points;
    private Instant createdAt;
}