package org.example.rawabet.dto.cinema.request;

import lombok.Data;
import org.example.rawabet.enums.cinema.HallType;
import org.example.rawabet.enums.cinema.ScreenType;

@Data
public class CreateSalleRequest {

    private Long cinemaId;

    private String name;

    private HallType hallType;

    private ScreenType screenType;

}
