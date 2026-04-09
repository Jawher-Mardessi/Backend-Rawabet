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
    private String content;
    private LocalDateTime createdAt;
}