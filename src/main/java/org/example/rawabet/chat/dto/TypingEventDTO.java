package org.example.rawabet.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingEventDTO {
    private String username;
}
