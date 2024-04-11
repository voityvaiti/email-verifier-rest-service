package com.myproject.emailverifierrestservice.service.abstraction;

import com.myproject.emailverifierrestservice.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<AppUser> getAll(Pageable pageable);

    AppUser getByEmail(String email);

    AppUser updatePassword(UUID id, String password);

    void enableUser(UUID id);

    void deletePasswordResetToken(UUID id);

}
