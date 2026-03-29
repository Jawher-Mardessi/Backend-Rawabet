package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Paiement;
import org.example.rawabet.services.IPaiementService;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/paiements")

@RequiredArgsConstructor

public class PaiementController {

    private final IPaiementService paiementService;

    @PostMapping("/cinema/{reservationId}")

    public Paiement payerCinema(

            @PathVariable Long reservationId,

            @RequestParam String methode){

        return paiementService

                .payerReservationCinema(reservationId,methode);

    }

    @PostMapping("/evenement/{reservationId}")

    public Paiement payerEvenement(

            @PathVariable Long reservationId,

            @RequestParam String methode){

        return paiementService

                .payerReservationEvenement(reservationId,methode);

    }

    @GetMapping("/{id}")

    public Paiement getPaiement(@PathVariable Long id){

        return paiementService.getPaiement(id);

    }

}