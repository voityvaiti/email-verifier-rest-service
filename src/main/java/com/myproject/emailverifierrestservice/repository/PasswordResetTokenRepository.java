package com.myproject.emailverifierrestservice.repository;

import com.myproject.emailverifierrestservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByUserId(UUID id);

    Optional<PasswordResetToken> findByToken(String token);

}
