package org.example.rawabet.exceptions;

import org.springframework.http.HttpStatus;

// ─── 400 Bad Request ───────────────────────────────────────
public class InvalidRequestException extends AppException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

// ─── 403 Forbidden ─────────────────────────────────────────
class ForbiddenOperationException extends AppException {
    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}