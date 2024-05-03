package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.service.abstraction.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;


@SpringBootTest(classes = EmailServiceImpl.class)
class EmailServiceImplTest {

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired
    EmailService emailService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "fromAddress", "test@example.com");
        ReflectionTestUtils.setField(emailService, "serverHost", "http://example.com");
        ReflectionTestUtils.setField(emailService, "apiPrefix", "/api");
    }

    @Test
    void sendEmailVerification_ShouldSendEmailWithCorrectContent() {

        String email = "user@example.com";
        String token = "verificationToken";
        String expectedSubject = "Email verification.";
        String expectedText = "http://example.com/api/auth/email-confirm/verificationToken";

        emailService.sendEmailVerification(email, token);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        verify(javaMailSender, times(1)).send((SimpleMailMessage) argThat(message -> {
            SimpleMailMessage mailMessage = (SimpleMailMessage) message;

            return Objects.requireNonNull(mailMessage.getTo())[0].equals(email) &&
                    Objects.equals(mailMessage.getSubject(), expectedSubject) &&
                    Objects.equals(mailMessage.getText(), expectedText);
        }));
    }

    @Test
    void sendPasswordResetToken_ShouldSendEmailWithCorrectContent() {

        String email = "user@example.com";
        String token = "resetToken";
        String expectedSubject = "Password reset.";
        String expectedText = "Your password reset token: " + token;

        emailService.sendPasswordResetToken(email, token);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        verify(javaMailSender, times(1)).send((SimpleMailMessage) argThat(message -> {
            SimpleMailMessage mailMessage = (SimpleMailMessage) message;

            return Objects.requireNonNull(mailMessage.getTo())[0].equals(email) &&
                    Objects.equals(mailMessage.getSubject(), expectedSubject) &&
                    Objects.equals(mailMessage.getText(), expectedText);
        }));
    }

}