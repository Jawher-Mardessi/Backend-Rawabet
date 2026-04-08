package org.example.rawabet.cinema.dto.request;

import lombok.Data;
import org.example.rawabet.cinema.enums.HallType;
import org.example.rawabet.cinema.enums.ScreenType;


@Data
public class CreateSalleRequest {

    private Long cinemaId;

    private String name;

    private HallType hallType;

    private ScreenType screenType;

}
