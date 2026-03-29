package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Facture;
import org.example.rawabet.services.IFactureService;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/factures")

@RequiredArgsConstructor

public class FactureController {

    private final IFactureService factureService;

    @GetMapping("/{id}")

    public Facture getFacture(@PathVariable Long id){

        return factureService.getFacture(id);

    }

}