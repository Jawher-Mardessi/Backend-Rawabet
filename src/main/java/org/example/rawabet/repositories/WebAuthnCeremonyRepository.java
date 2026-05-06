package org.example.rawabet.repositories;

import org.example.rawabet.entities.WebAuthnCeremony;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebAuthnCeremonyRepository extends JpaRepository<WebAuthnCeremony, String> {
    Optional<WebAuthnCeremony> findByRequestIdAndPurposeAndConsumedFalse(String requestId, WebAuthnCeremony.Purpose purpose);
}