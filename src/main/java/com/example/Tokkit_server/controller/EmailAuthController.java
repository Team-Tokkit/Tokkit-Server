package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.dto.request.EmailAuthRequest;
import com.example.Tokkit_server.dto.request.EmailVerifyRequest;
import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.service.EmailService;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

	private final EmailService emailService;

	@PostMapping("/send")
	public ApiResponse<String> sendAuthCode(@RequestBody @Valid EmailAuthRequest request) {
		emailService.sendAuthCode(request.getEmail());
		return ApiResponse.onSuccess("이메일 전송 완료");
	}

	@PostMapping("/verify")
	public ApiResponse<String> verifyAuthCode(@RequestBody @Valid EmailVerifyRequest request) {
		boolean verified = emailService.verifyAuthCode(request.getEmail(), request.getCode());
		if (!verified) {
			throw new GeneralException(ErrorStatus._BAD_REQUEST); // 인증 실패시
		}
		return ApiResponse.onSuccess("인증 성공");
	}
}
