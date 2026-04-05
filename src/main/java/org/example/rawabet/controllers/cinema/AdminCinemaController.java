package org.example.rawabet.controllers.cinema;


import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.CreateCinemaRequest;
import org.example.rawabet.dto.cinema.response.CinemaResponse;
import org.example.rawabet.services.IService.cinema.ICinemaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cinemas")

@RequiredArgsConstructor

public class AdminCinemaController {

    private final ICinemaService cinemaService;

    @PostMapping
    @PreAuthorize("hasAuthority('CINEMA_CREATE')")
    public CinemaResponse createCinema(
            @RequestBody CreateCinemaRequest request){

        return cinemaService.createCinema(request);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_DELETE')")
    public void disableCinema(@PathVariable Long id){

        cinemaService.disableCinema(id);

    }

}
