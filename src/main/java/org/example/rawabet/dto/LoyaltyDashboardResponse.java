package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.RewardType;

import java.util.List;

@Data
@Builder
public class LoyaltyDashboardResponse {
    private CarteFideliteResponse carte;
    private List<FidelityHistoryResponse> history;
    private List<RewardType> rewards;
}
