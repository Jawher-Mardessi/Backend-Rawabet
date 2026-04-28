package org.example.rawabet.dto.ml;

import lombok.Data;

@Data
public class AllPredictionsResponse {
    private ChurnPredictionResponse churn;
    private AnomalyPredictionResponse anomaly;
    private NextLevelPredictionResponse nextLevel;
    private RewardPredictionResponse reward;
}