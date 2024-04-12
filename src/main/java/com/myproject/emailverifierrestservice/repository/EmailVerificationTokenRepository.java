package com.myproject.emailverifierrestservice.repository;

import com.myproject.emailverifierrestservice.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByUserId(UUID id);

    Optional<EmailVerificationToken> findByToken(String token);

}
