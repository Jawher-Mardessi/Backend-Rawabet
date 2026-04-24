package org.example.rawabet.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsendEventDTO {
    private Long messageId;      // ID du message concerné
    private boolean forEveryone; // si true → afficher "Message supprimé", si false → ne rien faire chez les autres
}