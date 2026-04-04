package org.example.rawabet.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}