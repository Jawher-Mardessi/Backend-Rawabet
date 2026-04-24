package org.example.rawabet.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserRolesRequest {

    @NotEmpty(message = "Au moins un role est obligatoire")
    private List<String> roles;
}