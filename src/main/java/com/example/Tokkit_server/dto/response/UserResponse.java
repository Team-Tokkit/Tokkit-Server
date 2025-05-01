package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserResponse {

	public Long id;

	public String name;

	public String email;

	public String phoneNumber;

	//User Entity -> ResponseDto 변환 메서드
	public static UserResponse from(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.build();
	}
}