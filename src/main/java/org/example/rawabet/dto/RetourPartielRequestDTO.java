package org.example.rawabet.dto;

import jakarta.validation.constraints.*;

public class RetourPartielRequestDTO {

    @Min(value = 1, message = "La quantité retournée doit être supérieure à 0")
    private int quantiteRetournee;

    public RetourPartielRequestDTO() {}

    public int getQuantiteRetournee() { return quantiteRetournee; }
    public void setQuantiteRetournee(int quantiteRetournee) { this.quantiteRetournee = quantiteRetournee; }
}