package com.myproject.emailverifierrestservice.dto;

import com.myproject.emailverifierrestservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private UUID id;

    private String email;

    private boolean enabled;

    private Set<Role> roles;

}
