package org.example.rawabet.cinema.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.rawabet.cinema.enums.HallType;
import org.example.rawabet.cinema.enums.ScreenType;


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
