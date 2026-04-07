package org.example.rawabet.services;

import org.example.rawabet.entities.CategorieMateriel;
import org.example.rawabet.repositories.CategorieMaterielRepository;
import org.example.rawabet.services.ICategorieMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategorieMaterielService implements ICategorieMaterielService {

    @Autowired
    private CategorieMaterielRepository categorieRepository;

    @Override
    public CategorieMateriel addCategorie(CategorieMateriel categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public CategorieMateriel updateCategorie(CategorieMateriel categorie) {
        if (!categorieRepository.existsById(categorie.getId()))
            throw new RuntimeException("Catégorie introuvable avec l'id: " + categorie.getId());
        return categorieRepository.save(categorie);
    }

    @Override
    public void deleteCategorie(Long id) {
        if (!categorieRepository.existsById(id))
            throw new RuntimeException("Catégorie introuvable avec l'id: " + id);
        categorieRepository.deleteById(id);
    }

    @Override
    public CategorieMateriel getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'id: " + id));
    }

    @Override
    public List<CategorieMateriel> getAllCategories() {
        return categorieRepository.findAll();
    }
}