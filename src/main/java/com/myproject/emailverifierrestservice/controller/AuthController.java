package com.myproject.emailverifierrestservice.controller;

import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.dto.JwtTokenResponseDto;
import com.myproject.emailverifierrestservice.dto.PasswordChangeRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import com.myproject.emailverifierrestservice.service.abstraction.AuthService;
import com.myproject.emailverifierrestservice.service.abstraction.EmailService;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api-prefix}/auth")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponseDto> logIn(@RequestBody @Valid AuthRequestDto authRequestDto) {

        String token = authService.generateTokenByCredentials(authRequestDto.getEmail(), authRequestDto.getPassword());

        return ResponseEntity.ok(new JwtTokenResponseDto(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid AuthRequestDto authRequestDto)  {

        AppUser createdUser = authService.registerNewUser(authRequestDto);

        emailService.sendEmailVerification(createdUser.getId(), createdUser.getEmail());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/resend/email-confirmation/{email}")
    public ResponseEntity<Void> resendEmailConfirmation(@PathVariable("email") String email)  {

        AppUser user = userService.getByEmail(email);

        emailService.sendEmailVerification(user.getId(), user.getEmail());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-confirm/{token}")
    public ResponseEntity<Void> confirmEmail(@PathVariable("token") String id)  {

        userService.enableUser(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/send/reset-password-email/{email}")
    public ResponseEntity<Void> sendPasswordReset(@PathVariable("email") String email)  {

        AppUser user = userService.getByEmail(email);

        String token = authService.generatePasswordResetToken(user);
        emailService.sendPasswordResetToken(user.getEmail(), token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordChangeRequestDto passwordChangeRequestDto) {

        if(!authService.validatePasswordResetToken(passwordChangeRequestDto.getVerificationToken())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PasswordResetToken passwordResetToken = authService.getPasswordResetTokenByToken(passwordChangeRequestDto.getVerificationToken());

        userService.updatePassword(passwordResetToken.getUser().getId(), passwordChangeRequestDto.getNewPassword());

        userService.deletePasswordResetToken(passwordResetToken.getId());
        return ResponseEntity.ok().build();
    }
}
