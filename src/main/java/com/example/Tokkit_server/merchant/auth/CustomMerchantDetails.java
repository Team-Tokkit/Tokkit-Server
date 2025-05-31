package com.example.Tokkit_server.merchant.auth;

import com.example.Tokkit_server.merchant.entity.Merchant;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomMerchantDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String roles;
    private final String businessNumber;

    public CustomMerchantDetails(Long id, String email, String businessNumber, String password, String roles) {
        this.id = id;
        this.email = email;
        this.businessNumber = businessNumber;
        this.password = password;
        this.roles = roles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null && !roles.isBlank()) {
            for (String role : roles.split(",")) {
                authorities.add(new SimpleGrantedAuthority(role.trim()));
            }
        } else {
            authorities.add(new SimpleGrantedAuthority("MERCHANT"));
        }
        return authorities;
    }

    public Long getId() {return id;}

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

    public Merchant toMerchant() {
        return Merchant.builder()
                .id(this.id)
                .businessNumber(this.businessNumber)
                .email(this.email)
                .password(this.password)
                .roles(this.roles)
                .build();
    }
}
