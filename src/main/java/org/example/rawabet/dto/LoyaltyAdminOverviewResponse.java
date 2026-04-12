package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoyaltyAdminOverviewResponse {
    private CarteStatsResponse stats;
    private List<TopClientResponse> topClients;
}
