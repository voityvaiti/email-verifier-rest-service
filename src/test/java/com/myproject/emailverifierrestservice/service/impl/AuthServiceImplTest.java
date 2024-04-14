package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.EmailVerificationToken;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import com.myproject.emailverifierrestservice.entity.Role;
import com.myproject.emailverifierrestservice.exception.InvalidVerificationTokenException;
import com.myproject.emailverifierrestservice.repository.EmailVerificationTokenRepository;
import com.myproject.emailverifierrestservice.repository.PasswordResetTokenRepository;
import com.myproject.emailverifierrestservice.repository.UserRepository;
import com.myproject.emailverifierrestservice.security.jwt.JwtTokenUtil;
import com.myproject.emailverifierrestservice.service.abstraction.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthServiceImpl.class)
class AuthServiceImplTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordResetTokenRepository passwordResetTokenRepository;
    @MockBean
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @MockBean
    JwtTokenUtil jwtTokenUtil;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;


    private static final String token = "someToken";
    private static final LocalDateTime validExpiryDateTime = LocalDateTime.now().plusHours(1);
    private static final LocalDateTime invalidExpiryDateTime = LocalDateTime.now().minusHours(1);

    private static final AppUser user = new AppUser();
    private static final EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user);
    private static final PasswordResetToken passwordResetToken = new PasswordResetToken(user);


    @Test
    void validateEmailVerificationToken_shouldDoNothing_ifTokenIsValid() {

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setExpiryDateTime(validExpiryDateTime);

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(emailVerificationToken));

        assertDoesNotThrow(() -> authService.validateEmailVerificationToken(token));
    }

    @Test
    void validateEmailVerificationToken_shouldThrowInvalidTokenException_ifTokenIsInvalid() {

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(InvalidVerificationTokenException.class,
                () -> authService.validateEmailVerificationToken(token)
        );
    }

    @Test
    void validateEmailVerificationToken_shouldThrowInvalidTokenException_ifTokenIsExpired() {

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setExpiryDateTime(invalidExpiryDateTime);

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(emailVerificationToken));

        assertThrows(InvalidVerificationTokenException.class,
                () -> authService.validateEmailVerificationToken(token)
        );
    }

    @Test
    void validatePasswordResetToken_shouldDoNothing_ifTokenIsValid() {

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDateTime(validExpiryDateTime);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        assertDoesNotThrow(() -> authService.validatePasswordResetToken(token));
    }

    @Test
    void validatePasswordResetToken_shouldThrowInvalidTokenException_ifTokenIsInvalid() {

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(InvalidVerificationTokenException.class,
                () -> authService.validatePasswordResetToken(token)
        );
    }

    @Test
    void validatePasswordResetToken_shouldThrowInvalidTokenException_ifTokenIsExpired() {

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDateTime(invalidExpiryDateTime);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        assertThrows(InvalidVerificationTokenException.class,
                () -> authService.validatePasswordResetToken(token)
        );
    }

    @Test
    void generateAuthTokenByCredentials_shouldReturnToken_ifCredentialsAreValid() {
        String username = "testUser";
        String password = "testPassword";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, password, new ArrayList<>());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        String generatedToken = authService.generateAuthTokenByCredentials(username, password);

        assertEquals(token, generatedToken, "Generated token should match");
    }

    @Test
    void generateAuthTokenByCredentials_shouldThrowBadCredentialsException_ifCredentialsAreInvalid() {
        String username = "testUser";
        String password = "testPassword";

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.generateAuthTokenByCredentials(username, password),
                "Should throw BadCredentialsException for invalid credentials");
    }


    @Test
    void generateEmailVerificationToken_shouldReturnToken_ifTokenNotExist() {

        when(emailVerificationTokenRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(emailVerificationToken);

        String generatedToken = authService.generateEmailVerificationToken(user);

        assertNotNull(generatedToken, "Generated token should not be null");
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    void generateEmailVerificationToken_shouldReturnToken_ifTokenExist() {

        when(emailVerificationTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(emailVerificationToken));
        when(emailVerificationTokenRepository.save(emailVerificationToken)).thenReturn(emailVerificationToken);

        String generatedToken = authService.generateEmailVerificationToken(user);

        assertNotNull(generatedToken, "Generated token should not be null");
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    void generatePasswordResetToken_shouldReturnToken_ifTokenNotExist() {

        when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);

        String generatedToken = authService.generatePasswordResetToken(user);

        assertNotNull(generatedToken, "Generated token should not be null");
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void generatePasswordResetToken_shouldReturnToken_ifTokenExist() {

        when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(passwordResetToken));
        when(passwordResetTokenRepository.save(passwordResetToken)).thenReturn(passwordResetToken);

        String generatedToken = authService.generatePasswordResetToken(user);

        assertNotNull(generatedToken, "Generated token should not be null");
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }


    @Test
    void testRegisterNewUser() {

        String encodedPassword = "encodedPassword";

        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("test@mail.com");
        authRequestDto.setPassword("testPassword");

        AppUser expectedUser = new AppUser();
        expectedUser.setEmail(authRequestDto.getEmail());
        expectedUser.setPassword(encodedPassword);
        expectedUser.setRoles(Collections.singleton(Role.USER));
        expectedUser.setEnabled(false);

        when(passwordEncoder.encode(authRequestDto.getPassword())).thenReturn(encodedPassword);


        authService.registerNewUser(authRequestDto);

        verify(userRepository).save(expectedUser);
    }
}