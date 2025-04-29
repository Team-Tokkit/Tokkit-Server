package com.example.Tokkit_server.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	//Username(Email) 로 CustomUserDetail 을 가져오기
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		log.info("[ CustomUserDetailsService ] Email 을 이용하여 User 를 검색합니다.");
		Optional<User> userEntity = userRepository.findByEmail(email);
		if (userEntity.isPresent()) {
			User user = userEntity.get();
			return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getRoles());
		}
		throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
	}
}