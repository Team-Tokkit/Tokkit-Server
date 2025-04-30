package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.auth.CustomUserDetails;
import com.example.Tokkit_server.dto.request.CreateUserRequestDto;
import com.example.Tokkit_server.dto.request.EmailChangeRequest;
import com.example.Tokkit_server.dto.request.LoginRequest;
import com.example.Tokkit_server.dto.request.SimplePasswordResetRequest;
import com.example.Tokkit_server.dto.request.SimplePasswordVerificationRequest;
import com.example.Tokkit_server.dto.request.UpdateUserRequestDto;
import com.example.Tokkit_server.dto.request.UserInfoUpdateRequest;
import com.example.Tokkit_server.dto.response.UserResponse;
import com.example.Tokkit_server.service.EmailService;
import com.example.Tokkit_server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


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

	// 간편 비밀번호 재설정 시 이메일 전송
	@PostMapping("/simple-password/send-verification")
	public ApiResponse<?> sendSimplePasswordVerification(@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			emailService.sendSimplePasswordVerification(userDetails.getUsername());
			return ApiResponse.onSuccess(SuccessStatus._OK);
		} catch (Exception e) {
			throw new GeneralException(ErrorStatus.EMAIL_NOT_SEND);
		}
	}

	@PostMapping("/simple-password/verify")
	public ApiResponse<?> verifySimplePasswordCode(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SimplePasswordVerificationRequest request) {

		userService.verifySimplePasswordCode(userDetails.getUsername(), request.getCode());
		return ApiResponse.onSuccess(SuccessStatus._OK);
	}

	@PutMapping("/simple-password/update")
	public ApiResponse<?> updateSimplePassword(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SimplePasswordResetRequest request) {

		userService.updateSimplePassword(userDetails.getUsername(), request.getSimplePassword());
		return ApiResponse.onSuccess(SuccessStatus._OK);
	}


	// 내 정보 수정 (이름, 전화번호)
	@PutMapping("/info-update")
	public ApiResponse<?> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserInfoUpdateRequest requestDto) {
		userService.updateUserInfo(userDetails.getId(), requestDto);
		return ApiResponse.onSuccess(SuccessStatus._OK);
	}

	// 이메일 변경
	@PutMapping("/email-update")
	public ApiResponse<?> updateEmail(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody EmailChangeRequest requestDto) {
		userService.updateEmail(userDetails.getId(), requestDto);
		return ApiResponse.onSuccess(SuccessStatus._OK);
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
