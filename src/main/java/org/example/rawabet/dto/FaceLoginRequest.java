package org.example.rawabet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceLoginRequest {
    /** Image en base64 (avec ou sans header data:image/jpeg;base64,...) */
    private String image;
}