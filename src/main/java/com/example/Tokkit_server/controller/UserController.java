package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.request.CreateUserRequestDto;
import com.example.Tokkit_server.dto.request.LoginRequest;
import com.example.Tokkit_server.dto.request.UpdateUserRequestDto;
import com.example.Tokkit_server.dto.response.UserResponse;
import com.example.Tokkit_server.service.EmailService;
import com.example.Tokkit_server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final EmailService emailService;

	// 회원가입
	@PostMapping("/register")
	public ApiResponse<UserResponse> createUser(@RequestBody CreateUserRequestDto requestDto) {
		UserResponse response = userService.createUser(requestDto);
		return ApiResponse.onSuccess(response);
	}

	// 내정보 조회
	@GetMapping("/info")
	public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
		return ApiResponse.onSuccess(userService.getUser(userDetails.getUsername()));
	}

	// 비밀번호 변경
	@PutMapping("/password-update")
	public ApiResponse<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody UpdateUserRequestDto requestDto) {
		try {
			UserResponse response = userService.updateUser(userDetails.getUsername(), requestDto);
			return ApiResponse.onSuccess(response);
		} catch (IllegalArgumentException e) {
			return ApiResponse.onFailure("400", e.getMessage(), null);
		}
	}

	// 비밀번호 찾기
	@PostMapping("/findPw")
	public ApiResponse<?> passWordReissuance(@RequestParam("email") String email) {
		try {
			emailService.sendMessageForPassword(email);
			return ApiResponse.onSuccess(null);
		} catch (Exception e) {
			log.error("임시 비밀번호 발급 실패", e);
			return ApiResponse.onFailure("500", "임시 비밀번호 발급에 실패했습니다.", null);
		}
	}

	// 로그인 - 사용 x, swagger용 api
	@PostMapping("/login")
	public ApiResponse<?> login(@RequestBody LoginRequest loginRequest) {
		return ApiResponse.onSuccess(null);
	}

	// 로그아웃
	@PostMapping("/logout")
	public ApiResponse<?> logout(@AuthenticationPrincipal UserDetails userDetails) {
		// 실제 로그아웃 로직 추가 필요
		return ApiResponse.onSuccess(null);
	}
}
