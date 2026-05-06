package org.example.rawabet.exceptions;

import org.springframework.http.HttpStatus;

// ─── 404 ───────────────────────────────────────────────────
public class UserNotFoundException extends AppException {
    public UserNotFoundException(Long id) {
        super("Utilisateur introuvable (id=" + id + ")", HttpStatus.NOT_FOUND);
    }
    public UserNotFoundException(String email) {
        super("Utilisateur introuvable (email=" + email + ")", HttpStatus.NOT_FOUND);
    }
}