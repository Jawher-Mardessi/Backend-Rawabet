package org.example.rawabet.club.repositories;

import org.example.rawabet.club.entities.ClubEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubEventRepository extends JpaRepository<ClubEvent, Long> {

    List<ClubEvent> findByClubId(Long clubId);

}