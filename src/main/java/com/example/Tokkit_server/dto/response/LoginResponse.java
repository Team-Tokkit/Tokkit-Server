package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@Builder
public class LoginResponse {
	private String accessToken;
}
