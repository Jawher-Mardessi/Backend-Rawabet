package org.example.rawabet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.rawabet.enums.FilmCategory;
import org.example.rawabet.enums.FilmGenre;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 1000)
    private String description;

    private int duree;

    @Enumerated(EnumType.STRING)
    private FilmGenre genre;

    @Enumerated(EnumType.STRING)
    private FilmCategory categorie;

    private String trailerUrl;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Seance> seances;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacks;

}