package org.example.rawabet.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {

    private String name;
    private List<String> permissions;
}