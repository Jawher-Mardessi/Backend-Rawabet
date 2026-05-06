package org.example.rawabet.chat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chatSessionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean deleted = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean edited = false;

    private LocalDateTime editedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean spoiler = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}