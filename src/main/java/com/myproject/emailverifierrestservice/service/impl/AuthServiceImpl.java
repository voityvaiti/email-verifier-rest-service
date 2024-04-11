package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.dto.AuthRequestDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import com.myproject.emailverifierrestservice.entity.Role;
import com.myproject.emailverifierrestservice.exception.InvalidPasswordResetTokenException;
import com.myproject.emailverifierrestservice.exception.ResourceNotFoundException;
import com.myproject.emailverifierrestservice.repository.PasswordResetTokenRepository;
import com.myproject.emailverifierrestservice.repository.UserRepository;
import com.myproject.emailverifierrestservice.security.jwt.JwtTokenUtil;
import com.myproject.emailverifierrestservice.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final ModelMapper modelMapper = new ModelMapper();


    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean validatePasswordResetToken(String token) {

        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByToken(token);

        log.debug("Validating reset password token: {}", token);

        return optionalPasswordResetToken.map(
                        passwordResetToken -> passwordResetToken.getExpiryDateTime().isAfter(LocalDateTime.now()))
                .orElseThrow(InvalidPasswordResetTokenException::new);
    }

    @Override
    public String generateTokenByCredentials(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        log.debug("User is authenticated: {}", authentication.getName());

        return jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
    }

    @Override
    public String generatePasswordResetToken(AppUser user) {

        log.debug("Generating password reset token for User: {}", user);

        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByUserId(user.getId());

        if (optionalPasswordResetToken.isPresent()) {

            log.info("Password reset token for User {} is already exists and being refreshed." , user);

            PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
            passwordResetToken.refresh();

            return passwordResetTokenRepository.save(passwordResetToken).getToken();

        } else {

            PasswordResetToken passwordResetToken = passwordResetTokenRepository.save(new PasswordResetToken(user));

            log.info("Password reset token is created for User: {}", user);

            return passwordResetToken.getToken();
        }
    }

    @Override
    public PasswordResetToken getPasswordResetTokenByToken(String token) {
        return passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Token not found."));
    }

    @Override
    public AppUser registerNewUser(AuthRequestDto authRequestDto) {

        AppUser user = modelMapper.map(authRequestDto, AppUser.class);

        log.debug("Received User to register: {}", user);

        user.setRoles(Collections.singleton(Role.USER));
        user.setEnabled(false);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Saving User: {}", user);

        return userRepository.save(user);
    }

}
