package org.example.rawabet.controllers;

import org.example.rawabet.entities.Ticket;
import org.example.rawabet.services.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private ITicketService service;

    @PostMapping("/add")
    public Ticket add(@RequestBody Ticket t){
        return service.addTicket(t);
    }

    @PutMapping("/update")
    public Ticket update(@RequestBody Ticket t){
        return service.updateTicket(t);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        service.deleteTicket(id);
    }

    @GetMapping("/{id}")
    public Ticket getById(@PathVariable Long id){
        return service.getTicketById(id);
    }

    @GetMapping("/all")
    public List<Ticket> getAll(){
        return service.getAllTickets();
    }
}