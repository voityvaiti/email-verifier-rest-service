package com.myproject.emailverifierrestservice.service.abstraction;

import java.util.UUID;

public interface EmailService {

    void sendEmailVerification(UUID id, String email);

    void sendPasswordResetToken(String email, String token);

}
