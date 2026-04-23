package org.example.rawabet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "webauthn_ceremonies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnCeremony {

    public enum Purpose {
        REGISTRATION,
        AUTHENTICATION
    }

    @Id
    @Column(length = 64)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Purpose purpose;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String requestJson;

    @Column(length = 255)
    private String username;

    @Column(length = 128)
    private String userHandle;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean consumed = false;

    @PrePersist
    void initTimestamps() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}