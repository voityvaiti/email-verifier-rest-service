package com.myproject.emailverifierrestservice.dto;

import com.myproject.emailverifierrestservice.validation.annotation.PasswordConstraints;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequestDto {

    private String verificationToken;

    @PasswordConstraints
    private String newPassword;

}
