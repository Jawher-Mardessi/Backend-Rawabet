CREATE TABLE IF NOT EXISTS qr_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(36) NOT NULL UNIQUE,
    user_abonnement_id BIGINT NOT NULL UNIQUE,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scanned_at DATETIME NULL,

    CONSTRAINT fk_qr_user_abonnement
        FOREIGN KEY (user_abonnement_id)
        REFERENCES user_abonnement(id)
        ON DELETE CASCADE,

    INDEX idx_code (code),
    INDEX idx_user_abonnement_id (user_abonnement_id)
);
