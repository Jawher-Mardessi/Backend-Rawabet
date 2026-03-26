package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.SalleCinema;
import org.example.rawabet.services.ISalleCinemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salles")
@RequiredArgsConstructor

public class SalleCinemaController {

    private final ISalleCinemaService salleCinemaService;

    // CREATE
    @PostMapping("/add")
    public SalleCinema addSalle(@RequestBody SalleCinema salle) {
        return salleCinemaService.addSalle(salle);
    }

    // UPDATE
    @PutMapping("/update")
    public SalleCinema updateSalle(@RequestBody SalleCinema salle) {
        return salleCinemaService.updateSalle(salle);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public void deleteSalle(@PathVariable Long id) {
        salleCinemaService.deleteSalle(id);
    }

    // GET BY ID
    @GetMapping("/get/{id}")
    public SalleCinema getSalleById(@PathVariable Long id) {
        return salleCinemaService.getSalleById(id);
    }

    // GET ALL
    @GetMapping("/all")
    public List<SalleCinema> getAllSalles() {
        return salleCinemaService.getAllSalles();
    }

}