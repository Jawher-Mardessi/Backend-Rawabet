package org.example.rawabet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpersonateRequest {

    @NotNull(message = "targetUserId est obligatoire")
    private Long targetUserId;
}