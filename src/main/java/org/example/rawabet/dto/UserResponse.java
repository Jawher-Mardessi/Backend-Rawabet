package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String nom;
    private String email;
    private List<String> roles;
}