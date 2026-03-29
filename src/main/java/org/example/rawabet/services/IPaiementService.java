package org.example.rawabet.services;

import org.example.rawabet.entities.Paiement;

public interface IPaiementService {

    Paiement payerReservationCinema(Long reservationId,String methode);

    Paiement payerReservationEvenement(Long reservationId,String methode);

    Paiement getPaiement(Long id);

}