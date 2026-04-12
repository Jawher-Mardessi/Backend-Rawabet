package org.example.rawabet.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDTO {

    private Long id;
    private Long chatSessionId;

    // Expéditeur
    private Long userId;
    private String username;
    private String userEmail;

    private String content;
    private LocalDateTime createdAt;
}