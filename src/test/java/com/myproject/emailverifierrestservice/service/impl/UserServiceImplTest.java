package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.EmailVerificationToken;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import com.myproject.emailverifierrestservice.exception.ResourceNotFoundException;
import com.myproject.emailverifierrestservice.repository.EmailVerificationTokenRepository;
import com.myproject.emailverifierrestservice.repository.PasswordResetTokenRepository;
import com.myproject.emailverifierrestservice.repository.UserRepository;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserServiceImpl.class)
class UserServiceImplTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmailVerificationTokenRepository emailVerificationTokenRepository;
    @MockBean
    PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    PasswordEncoder passwordEncoder;


    @Autowired
    UserService userService;


    private static final UUID id = UUID.randomUUID();
    private static final String email = "test@example.com";
    private static final String token = "testToken";



    @Test
    void getAllTest() {

        Pageable pageable = Pageable.unpaged();
        Page<AppUser> mockPage = mock(Page.class);
        when(userRepository.findAll(pageable)).thenReturn(mockPage);

        Page<AppUser> result = userService.getAll(pageable);

        verify(userRepository).findAll(pageable);

        assertSame(mockPage, result);
    }

    @Test
    void getByEmail_shouldReturnUser_ifUserWasFound() {

        AppUser mockUser = new AppUser();
        mockUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        AppUser result = userService.getByEmail(email);

        verify(userRepository).findByEmail(email);

        assertEquals(mockUser, result);
    }

    @Test
    void getByEmail_shouldThrowException_ifUserWasNotFound() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getByEmail(email));

        verify(userRepository).findByEmail(email);
    }


    @Test
    void getEmailVerificationToken_shouldReturnToken_ifTokenWasFound() {

        EmailVerificationToken mockToken = new EmailVerificationToken();
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        EmailVerificationToken result = userService.getEmailVerificationToken(token);

        verify(emailVerificationTokenRepository).findByToken(token);

        assertEquals(mockToken, result);
    }

    @Test
    void getEmailVerificationToken_shouldThrowException_ifTokenWasNotFound() {

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getEmailVerificationToken(token));

        verify(emailVerificationTokenRepository).findByToken(token);
    }

    @Test
    void getPasswordResetToken_shouldReturnToken_ifTokenWasFound() {

        PasswordResetToken mockToken = new PasswordResetToken();
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        PasswordResetToken result = userService.getPasswordResetToken(token);

        verify(passwordResetTokenRepository).findByToken(token);

        assertEquals(mockToken, result);
    }

    @Test
    void getPasswordResetToken_shouldThrowException_ifTokenWasNotFound() {

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getPasswordResetToken(token));

        verify(passwordResetTokenRepository).findByToken(token);
    }

    @Test
    void updatePassword_shouldProperlyUpdatePassword() {

        String newPassword = "newPassword";
        AppUser mockUser = new AppUser();
        mockUser.setId(id);
        mockUser.setPassword("oldPassword");
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        AppUser expectedUser = new AppUser();
        expectedUser.setId(mockUser.getId());
        expectedUser.setPassword(encodedPassword);

        userService.updatePassword(id, newPassword);

        verify(userRepository).findById(id);
        verify(passwordEncoder).encode(newPassword);

        verify(userRepository).save(expectedUser);
    }

    @Test
    void enableUser_shouldEnableUser_ifUserIsDisabled() {

        AppUser mockUser = new AppUser();
        mockUser.setId(id);
        mockUser.setEnabled(false);

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        userService.enableUser(id);

        verify(userRepository).findById(id);
        assert mockUser.isEnabled();
        verify(userRepository).save(mockUser);
    }

    @Test
    void enableUser_shouldDoNothing_whenUserIsAlreadyEnabled() {

        AppUser mockUser = new AppUser();
        mockUser.setId(id);
        mockUser.setEnabled(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        userService.enableUser(id);

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(mockUser);
    }

    @Test
    void deleteEmailVerificationToken_shouldDeleteToken() {

        userService.deleteEmailVerificationToken(id);

        verify(emailVerificationTokenRepository).deleteById(id);
    }

    @Test
    void deletePasswordResetToken_shouldDeleteToken() {

        userService.deletePasswordResetToken(id);

        verify(passwordResetTokenRepository).deleteById(id);
    }


}