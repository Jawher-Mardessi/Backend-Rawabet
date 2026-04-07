package org.example.rawabet.services;

import org.example.rawabet.entities.CategorieMateriel;
import java.util.List;

public interface ICategorieMaterielService {
    CategorieMateriel addCategorie(CategorieMateriel categorie);
    CategorieMateriel updateCategorie(CategorieMateriel categorie);
    void deleteCategorie(Long id);
    CategorieMateriel getCategorieById(Long id);
    List<CategorieMateriel> getAllCategories();
}