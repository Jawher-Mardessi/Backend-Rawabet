package org.example.rawabet.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception métier de base.
 * Chaque sous-classe encode un statut HTTP précis,
 * ce qui permet au GlobalExceptionHandler de répondre
 * avec le bon code sans aucune logique supplémentaire.
 */
public class AppException extends RuntimeException {

    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}