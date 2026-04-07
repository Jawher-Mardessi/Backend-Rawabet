package org.example.rawabet.scheduler;

import org.example.rawabet.services.IReservationEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiryScheduler {

    @Autowired
    private IReservationEvenementService reservationService;

    // ✅ Runs every minute to expire pending reservations
    @Scheduled(fixedRate = 60000000)
    public void expireReservations() {
        reservationService.expireReservations();
    }
}