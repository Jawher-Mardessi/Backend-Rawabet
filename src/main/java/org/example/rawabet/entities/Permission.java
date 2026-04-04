package org.example.rawabet.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"roles"})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String module;  // ex: CINEMA

    @Column(nullable = false)
    private String action;  // ex: CREATE, READ, UPDATE, DELETE

    @Column(unique = true, nullable = false)
    private String name; // ex: CINEMA_CREATE

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

    //  → génération automatique du name
    @PrePersist
    @PreUpdate
    public void generateName() {
        if (module != null && action != null) {
            this.name = module.toUpperCase() + "_" + action.toUpperCase();
        }
    }
}