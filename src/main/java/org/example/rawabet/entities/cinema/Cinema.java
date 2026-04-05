package org.example.rawabet.entities.cinema;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cinemas")

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor

public class Cinema extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String address;

    private String city;

    private String country;

    private String phone;

    private String email;

    private Double latitude;

    private Double longitude;

    private String timezone;

    private String openingHours;

    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(
            mappedBy = "cinema",
            fetch = FetchType.LAZY
    )
    private List<SalleCinema> salles;



}