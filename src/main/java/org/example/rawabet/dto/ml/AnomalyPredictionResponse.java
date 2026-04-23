package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnomalyPredictionResponse {
    @JsonProperty("is_anomaly")
    private int isAnomaly;

    @JsonProperty("anomaly_label")
    private String anomalyLabel;

    private double probability;

    @JsonProperty("isolation_score")
    private double isolationScore;

    @JsonProperty("isolation_flag")
    private boolean isolationFlag;

    @JsonProperty("consensus_anomaly")
    private boolean consensusAnomaly;

    @JsonProperty("recommended_action")
    private String recommendedAction;
}