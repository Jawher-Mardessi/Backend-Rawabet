package org.example.rawabet.dto.cinema.request;

import lombok.Data;

@Data
public class ConfigureHallRequest {

    private Long salleId;

    private Integer numberOfRows;

    private Integer seatsPerRow;

}
