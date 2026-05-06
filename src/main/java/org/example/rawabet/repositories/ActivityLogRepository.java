package org.example.rawabet.repositories;

import org.example.rawabet.entities.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Filtrage par période
    List<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(Instant from, Instant to);

    // Filtrage par période + type
    List<ActivityLog> findByTypeAndTimestampBetweenOrderByTimestampDesc(
            String type, Instant from, Instant to
    );

    // Filtrage par plusieurs types
    @Query("SELECT a FROM ActivityLog a WHERE a.type IN :types " +
            "AND a.timestamp BETWEEN :from AND :to ORDER BY a.timestamp DESC")
    List<ActivityLog> findByTypesAndPeriod(
            @Param("types") List<String> types,
            @Param("from")  Instant from,
            @Param("to")    Instant to
    );

    // Comptage par type sur une période
    long countByTypeAndTimestampBetween(String type, Instant from, Instant to);
}