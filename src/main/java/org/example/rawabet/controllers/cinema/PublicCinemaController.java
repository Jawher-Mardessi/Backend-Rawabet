package org.example.rawabet.controllers.cinema;


import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.response.CinemaResponse;
import org.example.rawabet.services.IService.cinema.ICinemaService;
import org.example.rawabet.services.ServiceImpl.cinema.CinemaServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")

@RequiredArgsConstructor

public class PublicCinemaController {

    private final ICinemaService cinemaService;

    @GetMapping
    public List<CinemaResponse> getCinemas(){

        return cinemaService.getActiveCinemas();

    }

    @GetMapping("/{id}")
    public CinemaResponse getCinema(
            @PathVariable Long id){

        return cinemaService.getCinemaById(id);

    }

}
