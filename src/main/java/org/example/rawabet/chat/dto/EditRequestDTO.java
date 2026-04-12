package org.example.rawabet.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditRequestDTO {
    private Long messageId;
    private String newContent;
}