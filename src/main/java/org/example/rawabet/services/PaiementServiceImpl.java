package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.*;
import org.example.rawabet.enums.PaymentStatus;
import org.example.rawabet.enums.ReservationStatus;
import org.example.rawabet.repositories.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class PaiementServiceImpl implements IPaiementService {

    private final PaiementRepository paiementRepository;

    private final ReservationCinemaRepository reservationCinemaRepository;

    private final ReservationEvenementRepository reservationEvenementRepository;

    private final FactureRepository factureRepository;

    @Override

    public Paiement payerReservationCinema(Long reservationId,String methode){

        ReservationCinema reservation = reservationCinemaRepository.findById(reservationId)

                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if(reservation.getPaiement()!=null)

            throw new RuntimeException("Reservation already paid");

        double montant = reservation.getSeats()

                .stream()

                .mapToDouble(seat -> reservation.getSeance().getPrixBase())

                .sum();

        Paiement paiement = new Paiement();

        paiement.setMontant(montant);

        paiement.setDatePaiement(LocalDate.now());

        paiement.setStatut(PaymentStatus.SUCCESS);

        paiement.setReservationCinema(reservation);

        paiement.setMethode(methode);

        paiementRepository.save(paiement);

        reservation.setPaiement(paiement);

        reservation.setStatut(ReservationStatus.CONFIRMED);

        reservationCinemaRepository.save(reservation);

        return paiement;
    }

    @Override

    public Paiement payerReservationEvenement(Long reservationId,String methode){

        ReservationEvenement reservation = reservationEvenementRepository.findById(reservationId)

                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if(reservation.getPaiement()!=null)

            throw new RuntimeException("Reservation already paid");

        long hours = Duration.between(

                reservation.getDateDebut(),

                reservation.getDateFin()

        ).toHours();

        double salleCost = hours *

                reservation.getEvenement()

                        .getSalle()

                        .getPrixParHeure();

        double materielCost = reservation.getMateriels()

                .stream()

                .mapToDouble(m -> m.getPrix())

                .sum();

        double montant = salleCost + materielCost;

        Paiement paiement = new Paiement();

        paiement.setMontant(montant);

        paiement.setDatePaiement(LocalDate.now());

        paiement.setStatut(PaymentStatus.SUCCESS);

        paiement.setReservationEvenement(reservation);

        paiement.setMethode(methode);

        paiementRepository.save(paiement);

        reservation.setPaiement(paiement);

        reservation.setStatut(ReservationStatus.CONFIRMED);

        reservationEvenementRepository.save(reservation);

        return paiement;
    }

    @Override

    public Paiement getPaiement(Long id){

        return paiementRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Paiement not found"));

    }

}