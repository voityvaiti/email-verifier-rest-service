package com.myproject.emailverifierrestservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myproject.emailverifierrestservice.validation.annotation.EmailConstraints;
import com.myproject.emailverifierrestservice.validation.annotation.PasswordConstraints;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usr")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true)
    @EmailConstraints
    private String email;

    @Column(name = "password")
    @PasswordConstraints
    @ToString.Exclude
    @JsonIgnore
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @CreatedDate
    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

}
