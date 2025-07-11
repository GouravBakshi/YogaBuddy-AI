package com.yogabuddy.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String username;
    private Set<String> roles;
}
