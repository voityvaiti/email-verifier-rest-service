package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.service.abstraction.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Log4j2
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${email-from-address}")
    private String fromAddress;

    @Value("${server-host}")
    private String serverHost;
    @Value("${api-prefix}")
    private String apiPrefix;


    @Override
    public void sendEmailVerification(String email, String token) {

        log.debug("Sending email verification on email: {}", email);

        String emailConfirmationLink = serverHost +
                apiPrefix +
                "/auth/email-confirm/" +
                token;

        sendMessage(email, "Email verification.", emailConfirmationLink);
    }

    @Override
    public void sendPasswordResetToken(String email, String token) {

        log.debug("Sending password reset token on email: {}", email);

        String message = "Your password reset token: " + token;

        sendMessage(email, "Password reset.", message);
    }


    private void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        log.debug("Sending message to {}. Subject: {}", to, subject);

        javaMailSender.send(message);
    }
}
