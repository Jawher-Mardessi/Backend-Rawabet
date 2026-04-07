package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarteStatsResponse {
    private long totalClients;
    private long totalSilver;
    private long totalGold;
    private long totalVip;
    private long totalPointsDistribués;
}