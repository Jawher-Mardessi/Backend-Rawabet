package org.example.rawabet.repositories;

import org.example.rawabet.entities.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
}