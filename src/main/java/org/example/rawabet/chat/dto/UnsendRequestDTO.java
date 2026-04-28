package org.example.rawabet.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnsendRequestDTO {
    private Long messageId;
    private boolean forEveryone; // true = tous, false = juste moi
}