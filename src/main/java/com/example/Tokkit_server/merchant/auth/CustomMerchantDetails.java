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
    private final String name;
    private final String email;
    private final String password;
    private final String roles;
    private final String businessNumber;

    public CustomMerchantDetails(Merchant merchant) {
        this.id = merchant.getId();
        this.name = merchant.getName();
        this.email = merchant.getEmail();
        this.password = merchant.getPassword();
        this.roles = merchant.getRoles();
        this.businessNumber = merchant.getBusinessNumber();
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
