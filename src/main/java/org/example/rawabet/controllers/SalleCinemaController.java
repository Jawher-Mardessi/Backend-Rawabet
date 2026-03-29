package org.example.rawabet.controllers;

import org.example.rawabet.entities.SalleCinema;
import org.example.rawabet.services.ISalleCinemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salles")

public class SalleCinemaController {

    private final ISalleCinemaService salleCinemaService;

    public SalleCinemaController(ISalleCinemaService salleCinemaService) {
        this.salleCinemaService = salleCinemaService;
    }

    @PostMapping("/add")
    public SalleCinema addSalle(@RequestBody SalleCinema salle) {
        return salleCinemaService.addSalle(salle);
    }

    @PutMapping("/update")
    public SalleCinema updateSalle(@RequestBody SalleCinema salle) {
        return salleCinemaService.updateSalle(salle);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSalle(@PathVariable Long id) {
        salleCinemaService.deleteSalle(id);
    }

    @GetMapping("/get/{id}")
    public SalleCinema getSalleById(@PathVariable Long id) {
        return salleCinemaService.getSalleById(id);
    }

    @GetMapping("/all")
    public List<SalleCinema> getAllSalles() {
        return salleCinemaService.getAllSalles();
    }

}