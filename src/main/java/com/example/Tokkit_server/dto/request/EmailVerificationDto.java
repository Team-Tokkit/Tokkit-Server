package com.example.Tokkit_server.dto.request;

import lombok.Getter;

@Getter
public class EmailVerificationDto {
	String email;
	String verification;
}