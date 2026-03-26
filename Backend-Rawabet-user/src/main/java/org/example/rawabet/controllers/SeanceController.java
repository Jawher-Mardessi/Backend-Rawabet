package org.example.rawabet.controllers;

import org.example.rawabet.entities.Seance;
import org.example.rawabet.services.ISeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seances")
public class SeanceController {

    @Autowired
    private ISeanceService service;

    @PostMapping("/add")
    public Seance add(@RequestBody Seance s){
        return service.addSeance(s);
    }

    @PutMapping("/update")
    public Seance update(@RequestBody Seance s){
        return service.updateSeance(s);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        service.deleteSeance(id);
    }

    @GetMapping("/{id}")
    public Seance getById(@PathVariable Long id){
        return service.getSeanceById(id);
    }

    @GetMapping("/all")
    public List<Seance> getAll(){
        return service.getAllSeances();
    }
}