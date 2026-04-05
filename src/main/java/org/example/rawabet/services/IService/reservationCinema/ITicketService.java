package org.example.rawabet.services.IService.reservationCinema;

import org.example.rawabet.entities.Ticket;

import java.util.List;

public interface ITicketService {

    Ticket addTicket(Ticket ticket);

    Ticket updateTicket(Ticket ticket);

    void deleteTicket(Long id);

    Ticket getTicketById(Long id);

    List<Ticket> getAllTickets();
}
