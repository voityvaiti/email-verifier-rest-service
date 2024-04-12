package com.myproject.emailverifierrestservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "email_verification_token")

@Data
@NoArgsConstructor
public class EmailVerificationToken {

    private static final Duration lifetime = Duration.ofMinutes(60);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "expiry_datetime")
    private LocalDateTime expiryDateTime;


    @CreatedDate
    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;


    public EmailVerificationToken(AppUser user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.expiryDateTime = LocalDateTime.now().plus(lifetime);
    }


    public void refresh() {
        this.token = UUID.randomUUID().toString();
        this.expiryDateTime = LocalDateTime.now().plus(lifetime);
    }
}
