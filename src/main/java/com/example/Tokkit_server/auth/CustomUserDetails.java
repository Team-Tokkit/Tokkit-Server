package com.example.Tokkit_server.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.Tokkit_server.domain.user.User;

public class CustomUserDetails implements UserDetails {

	private final Long id;
	private final String name;
	private final String email;
	private final String password;
	private final String roles;

	public CustomUserDetails(Long id, String name, String email, String password, String roles) {
		this.id = id;
		this.name = name;
		this.email = email;
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
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return authorities;
	}

	public Long getId() { return id; }

	public String getName() {
		return name;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
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

	@Override
	public boolean isEnabled() {
		return true;
	}

	public User toUser() {
		return User.builder()
			.id(this.id)
			.email(this.email)
			.password(this.password)
			.roles(this.roles)
			.build();
	}
}
