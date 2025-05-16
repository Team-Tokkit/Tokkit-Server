package com.example.Tokkit_server.merchant.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.merchant.dto.request.MerchantEmailVerificatioRequestDto;
import com.example.Tokkit_server.merchant.service.MerchantEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
@Tag(name = "MerchantEmail", description = "가맹점주 이메일 인증 API입니다.")
public class MerchantEmailController {

    private final MerchantEmailService merchantEmailService;

    @PostMapping("/emailCheck")
    @Operation(summary = "가맹점주 이메일 인증 요청", description = "이메일 인증 코드 요청을 보내는 API입니다.")
    public ApiResponse<?> requestEmailValidation(@RequestParam String email) {
        try {
            merchantEmailService.sendMessage(email);
            return ApiResponse.onSuccess(SuccessStatus.EMAIL_OK);
        } catch (Exception e) {
            log.error("[EmailController] 이메일 인증 요청 실패", e);
            return ApiResponse.onFailure("500", "이메일 인증 요청에 실패했습니다.", null);
        }
    }

    @PostMapping("/verification")
    @Operation(summary = "인증번호 검증", description = "이메일 인증 코드를 검증하는 API입니다.")
    public ApiResponse<?> checkEmailValidation(@RequestBody MerchantEmailVerificatioRequestDto verificatioReqDto) {
        try {
            boolean isValid = merchantEmailService.ValidationCheck(
                    verificatioReqDto.getEmail(), verificatioReqDto.getVerification()
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
