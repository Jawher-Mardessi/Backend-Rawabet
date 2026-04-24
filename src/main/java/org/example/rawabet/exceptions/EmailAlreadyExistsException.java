package org.example.rawabet.exceptions;

import org.springframework.http.HttpStatus;

// ─── 409 Conflict ──────────────────────────────────────────
public class EmailAlreadyExistsException extends AppException {
    public EmailAlreadyExistsException(String email) {
        super("L'adresse email est déjà utilisée : " + email, HttpStatus.CONFLICT);
    }
}