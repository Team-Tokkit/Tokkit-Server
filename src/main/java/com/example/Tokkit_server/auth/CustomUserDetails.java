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
	private final String email;
	private final String password;
	private final String roles;

	public CustomUserDetails(Long id, String email, String password, String roles) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	// 해당 User 의 권한을 return
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(roles));
		return authorities;
	}

	public Long getId() { return id; }

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	// Account 가 만료되었는지?
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// Account 가 잠겨있는지?
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// Credential 만료되지 않았는지?
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 활성화가 되어있는지?
	@Override
	public boolean isEnabled() {
		// User Entity 에서 Status 가져온 후 true? false? 검사
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