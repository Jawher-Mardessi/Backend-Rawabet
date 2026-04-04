package org.example.rawabet.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoleResponse {

    private Long id;
    private String name;
    private List<String> permissions;
}