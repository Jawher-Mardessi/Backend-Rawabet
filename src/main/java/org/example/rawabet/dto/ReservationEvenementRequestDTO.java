package org.example.rawabet.dto;

import jakarta.validation.constraints.*;

public class ReservationEvenementRequestDTO {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'événement est obligatoire")
    private Long evenementId;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phoneNumber;

    public ReservationEvenementRequestDTO() {}

    public ReservationEvenementRequestDTO(Long userId, Long evenementId, String phoneNumber) {
        this.userId = userId;
        this.evenementId = evenementId;
        this.phoneNumber = phoneNumber;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEvenementId() { return evenementId; }
    public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}