package org.example.rawabet.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private Long userId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
