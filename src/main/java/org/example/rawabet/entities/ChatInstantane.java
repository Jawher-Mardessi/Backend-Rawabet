package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ChatInstantane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String message;

    private LocalDateTime horodatage;

    private boolean isActive;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name="seance_id")
    private Seance seance;

}