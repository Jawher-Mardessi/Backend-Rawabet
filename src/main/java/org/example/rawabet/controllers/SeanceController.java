package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.seance.request.CreateSeanceRequest;
import org.example.rawabet.dto.seance.response.SeanceResponse;
import org.example.rawabet.services.ISeanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seances")
@RequiredArgsConstructor
public class SeanceController {

    private final ISeanceService service;

    @PostMapping("/add")
    public SeanceResponse add(@RequestBody CreateSeanceRequest request){
        return service.addSeance(request);
    }

    @PutMapping("/update/{id}")
    public SeanceResponse update(@PathVariable Long id, @RequestBody CreateSeanceRequest request){
        return service.updateSeance(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        service.deleteSeance(id);
    }

    @GetMapping("/{id}")
    public SeanceResponse getById(@PathVariable Long id){
        return service.getSeanceById(id);
    }

    @GetMapping("/all")
    public List<SeanceResponse> getAll(){
        return service.getAllSeances();
    }
}