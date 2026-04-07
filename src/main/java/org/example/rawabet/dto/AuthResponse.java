package org.example.rawabet.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}