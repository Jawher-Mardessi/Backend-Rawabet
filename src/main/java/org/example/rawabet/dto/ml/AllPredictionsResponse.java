package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AllPredictionsResponse {
    private ChurnPredictionResponse churn;
    private AnomalyPredictionResponse anomaly;

    @JsonProperty("next_level")
    private NextLevelPredictionResponse nextLevel;

    private RewardPredictionResponse reward;
}