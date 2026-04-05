package org.example.rawabet.services.IService.reservationCinema;
import org.example.rawabet.entities.Seance;

import java.util.List;

public interface ISeanceService {

    Seance addSeance(Seance seance);

    Seance updateSeance(Seance seance);

    void deleteSeance(Long id);

    Seance getSeanceById(Long id);

    List<Seance> getAllSeances();
}