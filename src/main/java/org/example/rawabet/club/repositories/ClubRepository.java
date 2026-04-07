package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
}