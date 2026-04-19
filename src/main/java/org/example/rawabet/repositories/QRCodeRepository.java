package org.example.rawabet.repositories;

import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.UserAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {

    Optional<QRCode> findByCode(String code);

    Optional<QRCode> findByUserAbonnement(UserAbonnement userAbonnement);
}
