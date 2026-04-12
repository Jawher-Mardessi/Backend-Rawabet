package org.example.rawabet.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessagePageDTO {

    private List<ChatMessageResponseDTO> messages;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasMore; // true si d'autres pages existent avant celle-ci
}