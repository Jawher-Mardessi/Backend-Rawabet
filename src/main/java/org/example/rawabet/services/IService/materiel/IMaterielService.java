package org.example.rawabet.services.IService.materiel;

import org.example.rawabet.entities.Materiel;

import java.util.List;

public interface IMaterielService {

    Materiel addMateriel(Materiel materiel);

    Materiel updateMateriel(Materiel materiel);

    void deleteMateriel(Long id);

    Materiel getMaterielById(Long id);

    List<Materiel> getAllMateriels();
}