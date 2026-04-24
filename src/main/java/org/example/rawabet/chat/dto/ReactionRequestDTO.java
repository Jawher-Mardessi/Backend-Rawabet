package org.example.rawabet.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequestDTO {

    private Long messageId;
    private String emoji; // ex: "👍", "❤️", "😂", "😮", "😢"
}