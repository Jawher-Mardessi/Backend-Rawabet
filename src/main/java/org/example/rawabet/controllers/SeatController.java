package org.example.rawabet.controllers;

import org.example.rawabet.entities.Seat;
import org.example.rawabet.repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping("/seance/{seanceId}")
    public List<Seat> getSeatsBySeance(@PathVariable Long seanceId) {
        return seatRepository.findBySeanceId(seanceId);
    }
}
