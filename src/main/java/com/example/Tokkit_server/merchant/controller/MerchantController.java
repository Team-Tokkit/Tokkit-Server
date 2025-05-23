package com.example.Tokkit_server.merchant.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.merchant.dto.request.*;
import com.example.Tokkit_server.merchant.dto.response.MerchantRegisterResponseDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantResponseDto;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.merchant.service.MerchantEmailService;
import com.example.Tokkit_server.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
@Tag(name = "Merchant", description = "Merchant 관련 API입니다.")
public class MerchantController {

    private final MerchantService merchantService;
    private final MerchantEmailService merchantEmailService;
    private final MerchantRepository merchantRepository;

    @PostMapping("/register")
    @Operation(summary = "회원가입 요청", description = "회원가입 요청을 처리합니다.")
    public ApiResponse<MerchantRegisterResponseDto> createMerchant(@RequestBody CreateMerchantRequestDto requestDto) {
        MerchantRegisterResponseDto responseDto = merchantService.createMerchant(requestDto);
        return ApiResponse.onSuccess(responseDto);
    }

    @GetMapping("/info")
    @Operation(summary = "가맹점주 정보 조회", description = "가맹점주의 이름, 가맹점명, 사업자등록번호, 주소, 이메일, 전화번호를 조회합니다.")
    public ApiResponse<MerchantResponseDto> getMerchant(@AuthenticationPrincipal CustomMerchantDetails merchantDetails) {
        Long merchantId = merchantDetails.getId();
        return ApiResponse.onSuccess(merchantService.getInfo(merchantId));
    }

    @PostMapping("/findPw")
    @Operation(summary = "비밀번호 찾기(재설정)", description = "가맹점주의 비밀번호를 랜덤한 값으로 설정한 후 이메일로 전송합니다.")
    public ApiResponse<?> passwordReIssuance(@RequestParam("businessNumber") String businessNumber) {
        try {
            Optional<Merchant> optionalMerchant = merchantRepository.findByBusinessNumber(businessNumber);

            if (optionalMerchant.isEmpty()) {
                return ApiResponse.onFailure("404", "해당 사업자번호로 등록된 가맹점이 없습니다.", null);
            }

            Merchant merchant = optionalMerchant.get();

            String email = merchant.getEmail();

            if (email == null || email.isBlank()) {
                return ApiResponse.onFailure("400", "가맹점 정보에 이메일이 등록되어 있지 않습니다.", null);
            }

            merchantEmailService.sendMessageForPassword(email);
            return ApiResponse.onSuccess(Map.of("email", email));
        } catch (Exception e) {
            return ApiResponse.onFailure("500", "임시 비밀번호 발급에 실패했습니다.", null);
        }
    }

    @PutMapping("/password-update")
    @Operation(summary = "비밀번호 변경", description = "유저의 비밀번호를 변경합니다.")
    public ApiResponse<?> updateMerchantPassword(@AuthenticationPrincipal CustomMerchantDetails merchantDetails,
                                                 @RequestBody UpdateMerchantPasswordRequestDto requestDto) {
        try {
            MerchantResponseDto responseDto = merchantService.updateMerchantPassword(merchantDetails.getBusinessNumber(), requestDto);
            return ApiResponse.onSuccess(responseDto);
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PostMapping("/find-simple-password")
    @Operation(summary = "간편 비밀번호 초기화", description = "가맹점주가 간편 비밀번호를 잊었을 경우 랜덤 값으로 설정한 후 이메일로 전송합니다.")
    public ApiResponse<?> sendSimplePasswordVerification(@RequestParam("email") String email) {
            merchantEmailService.sendMessageForSimplePassword(email);
            return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping("/simple-password/verify")
    @Operation(summary = "간편 비밀번호 유효성 검증", description = "가맹점주가 입력한 간편 비밀번호가 가맹점주가 설정한 간편 비밀번호와 일치하는지 검증합니다.")
    public ApiResponse<?> verifySimplePasswordCode(
            @AuthenticationPrincipal CustomMerchantDetails merchantDetails,
            @RequestBody MerchantSimplePasswordVerificationRequestDto requestDto) {
            merchantService.verifySimplePassword(merchantDetails.getId(), requestDto.getSimplePassword());

        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PutMapping("/simple-password/update")
    @Operation(summary = "간편 비밀번호 재설정", description = "가맹점주의 간편 비밀번호를 수정합니다.")
    public ApiResponse<?> updateSimplePassword(
            @AuthenticationPrincipal CustomMerchantDetails merchantDetails,
            @RequestBody MerchantSimplePasswordResetRequestDto requestDto) {

        merchantService.updateSimplePassword(merchantDetails.getBusinessNumber(), requestDto.getSimplePassword());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    // 이메일 변경
    @PutMapping("/email-update")
    @Operation(summary = "이메일 변경", description = "가맹점주의 이메일을 수정합니다. (이 과정 이전에 바꾸고자 하는 이메일에 대해 이메일 인증이 필요합니다.)")
    public ApiResponse<?> updateEmail(@AuthenticationPrincipal CustomMerchantDetails merchantDetails,
                                      @RequestBody MerchantEmailChangeRequestDto requestDto) {
        merchantService.updateEmail(merchantDetails.getId(), requestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 진행합니다. 실제 사용은 되지 않으며 swagger용 api입니다.")
    public ApiResponse<?> login(@RequestBody MerchantLoginRequestDto loginRequest) {
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/logout")
    @Operation(summary = "가맹점 로그아웃", description = "JWT 환경에서 refreshToken 쿠키를 만료시킵니다.")
    public ApiResponse<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None") // 프론트와 도메인이 다르면 꼭 필요
                .maxAge(0)
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());
        return ApiResponse.onSuccess("로그아웃 완료");
    }

}
