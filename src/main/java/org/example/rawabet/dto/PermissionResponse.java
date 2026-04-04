package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {

    private Long id;
    private String name;
    private String module;
    private String action;
}