package com.example.Tokkit_server.merchant.auth;

import com.example.Tokkit_server.merchant.entity.Merchant;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomMerchantDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String role;
    private final String businessNumber;

    public CustomMerchantDetails(Long id, String email, String businessNumber, String password, String role) {
        this.id = id;
        this.email = email;
        this.businessNumber = businessNumber;
        this.password = password;
        this.role = role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_MERCHANT");
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
