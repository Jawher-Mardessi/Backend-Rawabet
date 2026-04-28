package org.example.rawabet.club.repositories;

import jakarta.persistence.LockModeType;
import org.example.rawabet.club.entities.ClubEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubEventRepository extends JpaRepository<ClubEvent, Long> {

    List<ClubEvent> findByClubId(Long clubId);

    // ✅ AJOUT : verrou pessimiste pour éviter la race condition sur reservedPlaces
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ClubEvent e WHERE e.id = :id")
    Optional<ClubEvent> findByIdWithLock(@Param("id") Long id);
}
