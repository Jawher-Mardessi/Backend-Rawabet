package org.example.rawabet.repositories;

import org.example.rawabet.entities.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByCode(String code);
    Optional<QRCode> findByUserAbonnementId(Long userAbonnementId);
    void deleteByUserAbonnementId(Long userAbonnementId);
}
