package org.example.rawabet.services.ServiceImpl.carteFidelite;

import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.repositories.CarteFideliteRepository;
import org.example.rawabet.services.IService.carteFidelite.ICarteFideliteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteFideliteServiceImpl implements ICarteFideliteService {

    private final CarteFideliteRepository carteRepository;

    public CarteFideliteServiceImpl(CarteFideliteRepository carteRepository) {
        this.carteRepository = carteRepository;
    }

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