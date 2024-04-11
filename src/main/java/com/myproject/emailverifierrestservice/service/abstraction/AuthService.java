package com.myproject.emailverifierrestservice.service.abstraction;

import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;


public interface AuthService {

    boolean validatePasswordResetToken(String token);

    String generateTokenByCredentials(String username, String password);

    String generatePasswordResetToken(AppUser user);

    PasswordResetToken getPasswordResetTokenByToken(String token);

    AppUser registerNewUser(AuthRequestDto authRequestDto);

}
