package com.example.Tokkit_server.user.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.request.*;
import com.example.Tokkit_server.user.dto.response.UserResponseDto;
import com.example.Tokkit_server.user.service.EmailService;
import com.example.Tokkit_server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API입니다.")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/register")
    @Operation(summary = "회원가입 요청", description = "회원가입 요청을 처리합니다.")
    public ApiResponse<UserResponseDto> createUser(@RequestBody CreateUserRequestDto requestDto) {
        UserResponseDto response = userService.createUser(requestDto);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/info")
    @Operation(summary = "내 정보 조회", description = "유저의 이름, 이메일, 전화번호를 조회합니다.")
    public ApiResponse<UserResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.onSuccess(userService.getUser(userDetails.getUsername()));
    }

    @PostMapping("/findPw")
    @Operation(summary = "비밀번호 찾기(재설성)", description = "유저의 비밀번호를 랜덤한 값으로 설정한 후 이메일로 전송합니다.")
    public ApiResponse<?> passwordReIssuance(@RequestParam("email") String email) {
        try {
            emailService.sendMessageForPassword(email);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            log.error("임시 비밀번호 발급 실패", e);
            return ApiResponse.onFailure("500", "임시 비밀번호 발급에 실패했습니다.", null);
        }
    }

    @PutMapping("/password-update")
    @Operation(summary = "비밀번호 변경", description = "유저의 비밀번호를 변경합니다.")
    public ApiResponse<?> updateUserPassword(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody UpdateUserPasswordRequestDto requestDto) {
        try {
            UserResponseDto response = userService.updateUserPassword(userDetails.getUsername(), requestDto);
            return ApiResponse.onSuccess(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure("400", e.getMessage(), null);
        }
    }

    @PostMapping("/simple-password/send-verification")
    @Operation(summary = "간편 비밀번호 재설정 시 이메일 전송", description = "유저가 간편 비밀번호를 재설정 하기 전에 이메일 인증을 하기 위해 인증 코드를 이메일로 전송합니다.")
    public ApiResponse<?> sendSimplePasswordVerification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            emailService.sendSimplePasswordVerification(userDetails.getUsername());
            return ApiResponse.onSuccess(SuccessStatus._OK);
        } catch (Exception e) {
            return ApiResponse.onFailure("500", e.getMessage(), null);
        }
    }

    @PostMapping("/simple-password/verify")
    @Operation(summary = "간편 비밀번호 재설정 시 이메일 인증", description = "유저가 간편 비밀번호를 재설정 하기 전에 이메일 인증 코드를 확인합니다.")
    public ApiResponse<?> verifySimplePasswordCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SimplePasswordVerificationRequestDto request) {

        userService.verifySimplePasswordCode(userDetails.getUsername(), request.getCode());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PutMapping("/simple-password/update")
    @Operation(summary = "간편 비밀번호 재설정", description = "유저의 간편 비밀번호를 수정합니다.")
    public ApiResponse<?> updateSimplePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SimplePasswordResetRequestDto request) {

        userService.updateSimplePassword(userDetails.getUsername(), request.getSimplePassword());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PutMapping("/info-update")
    @Operation(summary = "내 정보 수정", description = "유저의 이름, 전화번호를 수정합니다.")
    public ApiResponse<?> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody UserInfoUpdateRequestDto requestDto) {
        userService.updateUserInfo(userDetails.getId(), requestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    // 이메일 변경
    @PutMapping("/email-update")
    @Operation(summary = "이메일 변경", description = "유저의 이메일을 수정합니다.(이 과정 이전에 바꾸고자 하는 이메일에 대해 이메일 인증이 필요합니다)")
    public ApiResponse<?> updateEmail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestBody EmailChangeRequestDto requestDto) {
        userService.updateEmail(userDetails.getId(), requestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 진행합니다. 실제 사용은 되지 않으며 swagger용 api입니다.")
    public ApiResponse<?> login(@RequestBody LoginRequestDto loginRequest) {
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다. 실제 사용은 되지 않으며 swagger용 api입니다.")
    public ApiResponse<?> logout(@AuthenticationPrincipal UserDetails userDetails) {
        // 실제 로그아웃 로직 추가 필요
        return ApiResponse.onSuccess(null);
    }
}