package com.myproject.emailverifierrestservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.dto.JwtTokenResponseDto;
import com.myproject.emailverifierrestservice.dto.PasswordChangeRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.EmailVerificationToken;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import com.myproject.emailverifierrestservice.service.abstraction.AuthService;
import com.myproject.emailverifierrestservice.service.abstraction.EmailService;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockBean
    AuthService authService;

    @MockBean
    UserService userService;

    @MockBean
    EmailService emailService;

    @Autowired
    MockMvc mockMvc;

    @Value("${api-prefix}")
    private String apiPrefix;

    private static final String verificationToken = "verificationToken";
    private static final String email = "some@mail.com";
    private static final String password = "somePassword";
    private static final AppUser user = new AppUser();

    private static final AuthRequestDto authRequestDto = new AuthRequestDto(email, password);
    private static final EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
    private static final PasswordResetToken passwordResetToken = new PasswordResetToken();
    private static final PasswordChangeRequestDto passwordChangeRequestDto = new PasswordChangeRequestDto(verificationToken, "newPassword");

    @BeforeAll
    static void init() {
        user.setEmail(email);
        user.setPassword(password);

        emailVerificationToken.setUser(user);
        passwordResetToken.setUser(user);
    }


    @Test
    void logIn_shouldReturnOkStatus() throws Exception {

        when(authService.generateAuthTokenByCredentials(anyString(), anyString())).thenReturn(verificationToken);

        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void logIn_generateAndReturnToken() throws Exception {

        JwtTokenResponseDto expectedResponseDto = new JwtTokenResponseDto(verificationToken);

        when(authService.generateAuthTokenByCredentials(anyString(), anyString())).thenReturn(verificationToken);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequestDto)))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        JwtTokenResponseDto actualResponse = new ObjectMapper().readValue(responseBody, JwtTokenResponseDto.class);
        assertEquals(expectedResponseDto, actualResponse);

        verify(authService).generateAuthTokenByCredentials(email, password);
    }

    @Test
    void signUp_shouldReturnCreatedStatus() throws Exception {

        when(authService.registerNewUser(any(AuthRequestDto.class))).thenReturn(user);
        when(authService.generateEmailVerificationToken(any(AppUser.class))).thenReturn(verificationToken);

        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix+ "/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void signUp_shouldRegisterNewUser() throws Exception {

        when(authService.registerNewUser(any(AuthRequestDto.class))).thenReturn(user);
        when(authService.generateEmailVerificationToken(any(AppUser.class))).thenReturn(verificationToken);


        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix+ "/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequestDto)));

        verify(authService).registerNewUser(authRequestDto);
    }

    @Test
    void signUp_shouldSendEmailVerification() throws Exception {

        when(authService.registerNewUser(any(AuthRequestDto.class))).thenReturn(user);
        when(authService.generateEmailVerificationToken(any(AppUser.class))).thenReturn(verificationToken);


        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix+ "/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(authRequestDto)));

        verify(emailService).sendEmailVerification(user.getEmail(), verificationToken);
    }

    @Test
    void resendEmailConfirmation_shouldReturnOkStatus() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);
        when(authService.generateEmailVerificationToken(any(AppUser.class))).thenReturn(verificationToken);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix+ "/auth/resend/email-confirmation/{email}", email))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void resendEmailConfirmation_shouldSendEmailVerification() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);
        when(authService.generateEmailVerificationToken(any(AppUser.class))).thenReturn(verificationToken);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix+ "/auth/resend/email-confirmation/{email}", email));

        verify(userService).getByEmail(email);
        verify(emailService).sendEmailVerification(email, verificationToken);
    }

    @Test
    void confirmEmail_shouldReturnOkStatus() throws Exception {

        doNothing().when(authService).validateEmailVerificationToken(anyString());
        when(userService.getEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);

        doNothing().when(userService).enableUser(any(UUID.class));
        doNothing().when(userService).deleteEmailVerificationToken(any(UUID.class));


        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/email-confirm/{token}", verificationToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void confirmEmail_shouldValidateToken() throws Exception {

        doNothing().when(authService).validateEmailVerificationToken(anyString());
        when(userService.getEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);

        doNothing().when(userService).enableUser(any(UUID.class));
        doNothing().when(userService).deleteEmailVerificationToken(any(UUID.class));


        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/email-confirm/{token}", verificationToken));

        verify(authService).validateEmailVerificationToken(verificationToken);
    }

    @Test
    void confirmEmail_shouldEnableUserAndDeleteToken() throws Exception {

        doNothing().when(authService).validateEmailVerificationToken(anyString());
        when(userService.getEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);

        doNothing().when(userService).enableUser(any(UUID.class));
        doNothing().when(userService).deleteEmailVerificationToken(any(UUID.class));


        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/email-confirm/{token}", verificationToken));

        verify(userService).getEmailVerificationToken(verificationToken);

        verify(userService).enableUser(user.getId());
        verify(userService).deleteEmailVerificationToken(emailVerificationToken.getId());
    }

    @Test
    void sendPasswordReset_shouldReturnOkStatus() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);
        when(authService.generatePasswordResetToken(any(AppUser.class))).thenReturn(verificationToken);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/send/reset-password-email/{email}", email))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void sendPasswordReset_shouldGeneratePasswordResetToken() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);
        when(authService.generatePasswordResetToken(any(AppUser.class))).thenReturn(verificationToken);


        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/send/reset-password-email/{email}", email));

        verify(userService).getByEmail(email);
        verify(authService).generatePasswordResetToken(user);
    }

    @Test
    void sendPasswordReset_shouldSendPasswordResetTokenEmail() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);
        when(authService.generatePasswordResetToken(any(AppUser.class))).thenReturn(verificationToken);


        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/auth/send/reset-password-email/{email}", email));

        verify(emailService).sendPasswordResetToken(email, verificationToken);
    }

    @Test
    void changePassword_shouldReturnOkStatus() throws Exception {

        doNothing().when(authService).validatePasswordResetToken(anyString());
        when(userService.getPasswordResetToken(anyString())).thenReturn(passwordResetToken);
        doNothing().when(userService).deletePasswordResetToken(any(UUID.class));

        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix + "/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordChangeRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void changePassword_shouldValidatePasswordResetToken() throws Exception {

        doNothing().when(authService).validatePasswordResetToken(anyString());
        when(userService.getPasswordResetToken(anyString())).thenReturn(passwordResetToken);
        doNothing().when(userService).deletePasswordResetToken(any(UUID.class));

        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix + "/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordChangeRequestDto)));

        verify(authService).validatePasswordResetToken(verificationToken);

    }

    @Test
    void changePassword_shouldUpdatePasswordAndDeleteToken() throws Exception {

        doNothing().when(authService).validatePasswordResetToken(anyString());
        when(userService.getPasswordResetToken(anyString())).thenReturn(passwordResetToken);
        doNothing().when(userService).deletePasswordResetToken(any(UUID.class));


        mockMvc.perform(MockMvcRequestBuilders.post(apiPrefix + "/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordChangeRequestDto)));

        verify(userService).getPasswordResetToken(verificationToken);
        verify(userService).updatePassword(user.getId(), passwordChangeRequestDto.getNewPassword());
        verify(userService).deletePasswordResetToken(passwordResetToken.getId());
    }



}