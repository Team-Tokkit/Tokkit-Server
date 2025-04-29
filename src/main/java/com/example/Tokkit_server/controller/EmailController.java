package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.dto.request.EmailVerificationDto;
import com.example.Tokkit_server.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "이메일 인증 API", description = "이메일 인증 API입니다.")
public class EmailController {

	private final EmailService emailService;

	// 이메일 인증 요청
	@PostMapping("/emailCheck")
	public ApiResponse<?> requestEmailValidation(@RequestParam String email) {
		try {
			emailService.sendMessage(email);
			return ApiResponse.onSuccess(SuccessStatus.EMAIL_OK);
		} catch (Exception e) {
			log.error("[EmailController] 이메일 인증 요청 실패", e);
			return ApiResponse.onFailure("500", "이메일 인증 요청에 실패했습니다.", null);
		}
	}

	// 인증번호 검증
	@PostMapping("/verification")
	public ApiResponse<?> checkEmailValidation(@RequestBody EmailVerificationDto verificationDto) {
		try {
			boolean isValid = emailService.ValidationCheck(
				verificationDto.getEmail(), verificationDto.getVerification()
			);

			if (isValid) {
				return ApiResponse.onSuccess(SuccessStatus.VERIFY_OK);
			} else {
				return ApiResponse.onFailure("400", "인증 실패: 인증번호가 일치하지 않습니다.", null);
			}

		} catch (Exception e) {
			log.error("[EmailController] 이메일 인증 검증 실패", e);
			return ApiResponse.onFailure("500", "이메일 인증 검증에 실패했습니다.", null);
		}
	}
}
