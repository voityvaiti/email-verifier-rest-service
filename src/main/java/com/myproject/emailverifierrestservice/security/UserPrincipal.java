package com.myproject.emailverifierrestservice.security;

import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final String email;

    private final String password;

    private final Set<Role> roles;

    private final boolean isEnabled;

    public UserPrincipal(AppUser user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.isEnabled = user.isEnabled();
    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
