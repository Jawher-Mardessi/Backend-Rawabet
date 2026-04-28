package org.example.rawabet.chat.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionEventDTO {

    private Long messageId;

    // emoji → count  ex: {"👍": 3, "❤️": 1}
    private Map<String, Long> counts;

    // emoji → liste de noms  ex: {"👍": ["Alice", "Bob"], "❤️": ["Farouk"]}
    private Map<String, List<String>> users;
}