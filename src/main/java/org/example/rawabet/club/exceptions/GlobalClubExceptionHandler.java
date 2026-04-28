package org.example.rawabet.club.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalClubExceptionHandler {

  // ── 404 Not Found ─────────────────────────────────────────────────────────
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFound(NotFoundException ex) {
    return error(ex.getMessage());
  }

  // ── 400 Business Rule ─────────────────────────────────────────────────────
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleBusiness(BusinessException ex) {
    return error(ex.getMessage());
  }

  // ── 400 Bean Validation (@Valid) ──────────────────────────────────────────
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
    var fieldError = ex.getBindingResult().getFieldError();
    String msg = fieldError != null
            ? fieldError.getDefaultMessage()
            : "Validation error";
    return error(msg);
  }

  // ── 409 Conflict — contrainte DB (doublon unique, FK violation…) ──────────
  // ✅ FIX AJOUTÉ : évite le 500 générique quand Hibernate lève une contrainte
  // unique (ex: double réservation concurrente passée sous le radar du lock,
  // ou double ClubMember sur même user+club).
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, String> handleDataIntegrity(DataIntegrityViolationException ex) {
    // On ne logue pas le détail SQL à l'utilisateur
    return error("Cette action n'est pas possible : contrainte d'intégrité violée. " +
            "Vérifiez qu'il n'existe pas déjà un enregistrement identique.");
  }

  // ── Utilitaire ────────────────────────────────────────────────────────────
  private Map<String, String> error(String message) {
    Map<String, String> body = new HashMap<>();
    body.put("error", message);
    return body;
  }
}