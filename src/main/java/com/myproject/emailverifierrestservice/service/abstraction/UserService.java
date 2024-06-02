package com.myproject.emailverifierrestservice.service.abstraction;

import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.EmailVerificationToken;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    boolean existsWithEmail(String email);

    Page<AppUser> getAll(Pageable pageable);

    AppUser getByEmail(String email);

    EmailVerificationToken getEmailVerificationToken(String token);

    PasswordResetToken getPasswordResetToken(String token);

    AppUser updatePassword(UUID id, String password);

    void enableUser(UUID id);

    void deleteEmailVerificationToken(UUID id);

    void deletePasswordResetToken(UUID id);

}
