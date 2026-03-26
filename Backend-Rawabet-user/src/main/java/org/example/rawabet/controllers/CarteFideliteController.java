package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.CarteFidelite;
import org.example.rawabet.services.ICarteFideliteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carte")
@RequiredArgsConstructor
public class CarteFideliteController {

    private final ICarteFideliteService carteService;

    // CREATE
    @PostMapping("/add")
    public CarteFidelite addCarte(@RequestBody CarteFidelite carte) {
        return carteService.addCarte(carte);
    }

    // UPDATE
    @PutMapping("/update")
    public CarteFidelite updateCarte(@RequestBody CarteFidelite carte) {
        return carteService.updateCarte(carte);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public void deleteCarte(@PathVariable Long id) {
        carteService.deleteCarte(id);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public CarteFidelite getCarte(@PathVariable Long id) {
        return carteService.getCarteById(id);
    }

    // GET ALL
    @GetMapping("/all")
    public List<CarteFidelite> getAll() {
        return carteService.getAllCartes();
    }
}