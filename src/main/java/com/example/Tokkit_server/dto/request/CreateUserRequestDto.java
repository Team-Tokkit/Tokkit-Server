package com.example.Tokkit_server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Tokkit_server.domain.user.User;

@NoArgsConstructor
@Getter
public class CreateUserRequestDto {

	public String name;

	public String email;

	public String password;

	public String phoneNumber;

	public String simplePassword;

	//User Dto -> User Entity로 변환하는 메서드
	public User toEntity(PasswordEncoder passwordEncoder) {
		//Password Encoder 통해 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(password);
		String encodedSimplePassword = passwordEncoder.encode(simplePassword);
		return User.builder()
			.name(name)
			.email(email)
			.password(encodedPassword) //암호화된 비밀번호 저장
			.phoneNumber(phoneNumber)
			.simplePassword(encodedSimplePassword)
			.isDormant(false)
			.roles("USER")
			.build();
	}

}