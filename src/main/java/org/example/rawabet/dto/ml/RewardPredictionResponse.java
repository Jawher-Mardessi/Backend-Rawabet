package org.example.rawabet.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RewardPredictionResponse {
    @JsonProperty("best_reward")
    private String bestReward;

    @JsonProperty("can_redeem")
    private boolean canRedeem;

    @JsonProperty("points_required")
    private int pointsRequired;

    @JsonProperty("points_available")
    private int pointsAvailable;

    @JsonProperty("top_rewards")
    private List<Map<String, Object>> topRewards;
}