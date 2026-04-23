package org.example.rawabet.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Existant — inchangé
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    // AJOUT 1 — filet race condition email (contrainte UNIQUE DB)
    // Intercepte le DataIntegrityViolationException lancé dans UserServiceImpl
    // quand deux inscriptions simultanées passent checkEmail() en même temps.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException e) {
        String msg = e.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("email")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already exists"));
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Violation de contrainte d'intégrité"));
    }

    // AJOUT 2 — erreurs @Valid : retourne tous les champs invalides d'un coup
    // au lieu d'une erreur 500 générique
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valeur invalide",
                        (a, b) -> a
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Données invalides",
                        "fields", fieldErrors
                ));
    }
}