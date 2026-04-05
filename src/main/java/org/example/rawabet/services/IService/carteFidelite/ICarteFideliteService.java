package org.example.rawabet.services.IService.carteFidelite;

import org.example.rawabet.entities.CarteFidelite;

import java.util.List;

public interface ICarteFideliteService {

    CarteFidelite addCarte(CarteFidelite carte);

    CarteFidelite updateCarte(CarteFidelite carte);

    void deleteCarte(Long id);

    CarteFidelite getCarteById(Long id);

    List<CarteFidelite> getAllCartes();
}