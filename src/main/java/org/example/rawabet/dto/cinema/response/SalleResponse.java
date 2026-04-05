package org.example.rawabet.dto.cinema.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.enums.cinema.HallType;
import org.example.rawabet.enums.cinema.ScreenType;

@Data
@Builder

public class SalleResponse {

    private Long id;

    private String name;

    private HallType hallType;

    private ScreenType screenType;

    private Integer totalCapacity;

    private Boolean isActive;

}
