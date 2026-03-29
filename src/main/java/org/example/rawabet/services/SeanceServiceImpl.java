package org.example.rawabet.services;

import org.example.rawabet.entities.Seance;
import org.example.rawabet.entities.Seat;
import org.example.rawabet.repositories.SeanceRepository;
import org.example.rawabet.repositories.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class SeanceServiceImpl implements ISeanceService {

    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;

    public SeanceServiceImpl(
            SeanceRepository seanceRepository,
            SeatRepository seatRepository){

        this.seanceRepository = seanceRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public Seance addSeance(Seance seance){

        // 1 sauvegarder séance
        Seance savedSeance = seanceRepository.save(seance);

        // 2 récupérer capacité salle
        int capacite = savedSeance.getSalleCinema().getCapacite();

        // 3 générer seats automatiquement
        for(int i = 1; i <= capacite; i++){

            Seat seat = new Seat();

            seat.setNumero(i);

            seat.setSeance(savedSeance);

            seat.setReservation(null);

            seatRepository.save(seat);
        }

        return savedSeance;
    }

    @Override
    public Seance updateSeance(Seance seance){
        return seanceRepository.save(seance);
    }

    @Override
    public void deleteSeance(Long id){
        seanceRepository.deleteById(id);
    }

    @Override
    public Seance getSeanceById(Long id){
        return seanceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Seance> getAllSeances(){
        return seanceRepository.findAll();
    }

}