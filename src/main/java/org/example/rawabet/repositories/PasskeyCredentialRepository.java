package org.example.rawabet.repositories;

import org.example.rawabet.entities.PasskeyCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredential, Long> {
    Optional<PasskeyCredential> findByCredentialIdAndUserHandle(String credentialId, String userHandle);

    List<PasskeyCredential> findByCredentialId(String credentialId);

    List<PasskeyCredential> findByUserHandle(String userHandle);
}