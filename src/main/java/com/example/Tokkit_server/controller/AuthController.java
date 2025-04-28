package com.example.Tokkit_server.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.request.LoginRequest;
import com.example.Tokkit_server.dto.request.SignUpRequest;
import com.example.Tokkit_server.dto.response.LoginResponse;
import com.example.Tokkit_server.dto.response.SignUpResonse;
import com.example.Tokkit_server.service.AuthService;
import com.example.Tokkit_server.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final AuthService authService;

	@PostMapping("/signup")
	public ApiResponse<SignUpResonse> signup(@RequestBody SignUpRequest request) {
		return ApiResponse.onSuccess(userService.signup(request));
	}

	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
		return ApiResponse.onSuccess(authService.login(request));
	}
}
