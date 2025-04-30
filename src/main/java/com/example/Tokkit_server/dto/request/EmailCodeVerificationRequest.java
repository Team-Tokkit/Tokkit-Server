package com.example.Tokkit_server.dto.request;

import lombok.Getter;

@Getter
public class EmailCodeVerificationRequest {
	private String email;
	private String code;
}