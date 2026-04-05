package org.example.rawabet.controllers.cinema;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.cinema.request.ConfigureHallRequest;
import org.example.rawabet.dto.cinema.response.SeatResponse;
import org.example.rawabet.services.IService.cinema.ISeatService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")

@RequiredArgsConstructor

public class SeatController {

    private final ISeatService seatService;

    @PostMapping("/configure")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void configureHall(
            @RequestBody ConfigureHallRequest request){

        seatService.configureHall(request);

    }

    @GetMapping("/salle/{salleId}")
    public List<SeatResponse> getSeats(
            @PathVariable Long salleId){

        return seatService.getSeatsBySalle(salleId);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CINEMA_UPDATE')")
    public void disableSeat(
            @PathVariable Long id){

        seatService.disableSeat(id);

    }

}