package org.example.rawabet.cinema.dto.request;

import lombok.Data;
import org.example.rawabet.cinema.enums.SeatType;
import java.util.List;

@Data
public class ConfigureHallRequest {

    private Long salleId;

    // Ancien mode (gardé pour compatibilité)
    private Integer numberOfRows;
    private Integer seatsPerRow;

    // Nouveau mode : config par rangée
    private List<RowConfig> rowConfigs;

    @Data
    public static class RowConfig {
        private String rowLabel;
        private Integer seatsPerRow;
        private SeatType seatType;
    }
}