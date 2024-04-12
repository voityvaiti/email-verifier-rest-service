package com.myproject.emailverifierrestservice.service.abstraction;


public interface EmailService {

    void sendEmailVerification(String email, String token);

    void sendPasswordResetToken(String email, String token);

}
