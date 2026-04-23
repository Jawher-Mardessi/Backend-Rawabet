package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class NextLevelPredictionResponse {
    @JsonProperty("current_level")
    private String currentLevel;

    @JsonProperty("predicted_level")
    private String predictedLevel;

    @JsonProperty("will_upgrade")
    private boolean willUpgrade;

    @JsonProperty("upgrade_message")
    private String upgradeMessage;

    @JsonProperty("points_for_gold")
    private int pointsForGold;

    @JsonProperty("points_for_vip")
    private int pointsForVip;

    private Map<String, Double> probabilities;
}