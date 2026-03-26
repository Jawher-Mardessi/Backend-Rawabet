package org.example.rawabet.services;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarteFideliteServiceImpl implements ICarteFideliteService {

    private final CarteFideliteRepository carteRepository;

    @Override
    public CarteFidelite addCarte(CarteFidelite carte) {
        return carteRepository.save(carte);
    }

    @Override
    public CarteFidelite updateCarte(CarteFidelite carte) {
        return carteRepository.save(carte);
    }

    @Override
    public void deleteCarte(Long id) {
        carteRepository.deleteById(id);
    }

    @Override
    public CarteFidelite getCarteById(Long id) {
        return carteRepository.findById(id).orElse(null);
    }

    @Override
    public List<CarteFidelite> getAllCartes() {
        return carteRepository.findAll();
    }
}