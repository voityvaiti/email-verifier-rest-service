package com.myproject.emailverifierrestservice.dto;

import com.myproject.emailverifierrestservice.validation.annotation.EmailConstraints;
import com.myproject.emailverifierrestservice.validation.annotation.PasswordConstraints;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    @EmailConstraints
    private String email;

    @PasswordConstraints
    private String password;

}
