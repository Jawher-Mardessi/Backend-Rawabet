package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChurnPredictionResponse {
    private int churn;

    @JsonProperty("churn_label")
    private String churnLabel;

    private double probability;

    @JsonProperty("risk_level")
    private String riskLevel;

    private String message;
}