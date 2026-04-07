package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.RewardType;

@Data
@Builder
public class RewardResponse {
    private RewardType reward;
    private int pointsDepensés;
    private int pointsRestants;
    private String message;
}