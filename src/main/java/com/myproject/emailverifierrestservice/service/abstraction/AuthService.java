package com.myproject.emailverifierrestservice.service.abstraction;

import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;


public interface AuthService {

    void validateEmailVerificationToken(String token);

    void validatePasswordResetToken(String token);

    String generateAuthTokenByCredentials(String username, String password);

    String generateEmailVerificationToken(AppUser user);

    String generatePasswordResetToken(AppUser user);

    AppUser registerNewUser(AuthRequestDto authRequestDto);

}
