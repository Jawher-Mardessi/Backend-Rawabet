package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.Facture;
import org.example.rawabet.repositories.FactureRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class FactureServiceImpl implements IFactureService {

    private final FactureRepository factureRepository;

    @Override
    public Facture getFacture(Long id){

        return factureRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Facture not found"));
    }

}