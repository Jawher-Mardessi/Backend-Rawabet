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
    private Long userId;
    private String username;
    private String userEmail;
    private String content;
    private LocalDateTime createdAt;
    private boolean deleted;
    private boolean edited;
    private LocalDateTime editedAt;
    private boolean spoiler;
}