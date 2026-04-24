package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransferRecipientResponse {
    private Long id;
    private String nom;
    private String email;
}
