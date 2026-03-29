package org.example.rawabet.controllers;

import org.example.rawabet.entities.Seat;
import org.example.rawabet.services.ISeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")

public class SeatController {

    private final ISeatService seatService;

    public SeatController(ISeatService seatService){
        this.seatService = seatService;
    }

    @PostMapping("/add")
    public Seat addSeat(@RequestBody Seat seat){
        return seatService.addSeat(seat);
    }

    @PutMapping("/update")
    public Seat updateSeat(@RequestBody Seat seat){
        return seatService.updateSeat(seat);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSeat(@PathVariable Long id){
        seatService.deleteSeat(id);
    }

    @GetMapping("/get/{id}")
    public Seat getSeatById(@PathVariable Long id){
        return seatService.getSeatById(id);
    }

    @GetMapping("/all")
    public List<Seat> getAllSeats(){
        return seatService.getAllSeats();
    }

}