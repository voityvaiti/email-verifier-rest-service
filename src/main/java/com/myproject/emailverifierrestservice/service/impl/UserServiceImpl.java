package com.myproject.emailverifierrestservice.service.impl;

import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.exception.ResourceNotFoundException;
import com.myproject.emailverifierrestservice.repository.PasswordResetTokenRepository;
import com.myproject.emailverifierrestservice.repository.UserRepository;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<AppUser> getAll(Pageable pageable) {

        log.debug("Getting User page: {}", pageable.toString());

        return userRepository.findAll(pageable);
    }

    @Override
    public AppUser getByEmail(String email) {

        log.debug("Looking for User by email: {}", email);

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public AppUser updatePassword(UUID id, String password) {
        AppUser user = getById(id);

        user.setPassword(passwordEncoder.encode(password));

        log.info("Updating password for User: {}", user);
        return userRepository.save(user);
    }

    @Override
    public void enableUser(UUID id) {

        AppUser user = getById(id);

        if (!user.isEnabled()) {

            log.info("Enabling User: {}", user);

            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    @Override
    public void deletePasswordResetToken(UUID id) {

        log.debug("Removing PasswordResetToken with ID: {}", id);

        passwordResetTokenRepository.deleteById(id);
    }

    private AppUser getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

}
