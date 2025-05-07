package com.example.Tokkit_server.notification.controller;

import java.util.List;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.notification.dto.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.service.NotificationSettingService;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.response.NotificationCategorySettingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications/setting")
@RequiredArgsConstructor
@Tag(name = "NotificationSetting", description = "알림 설정 관련 API입니다.")
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    @Operation(summary = "알림 설정 상태 조회", description = "유저의 알림 카테고리 설정 목록을 조회합니다.")
    public ApiResponse<List<NotificationCategorySettingResponseDto>> getSettings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(notificationSettingService.getSettings(userDetails.getId()));
    }

    @PutMapping
    @Operation(summary = "알림 설정 상태 수정", description = "유저의 알림 카테고리 설정을 수정합니다.")
    public ApiResponse<?> updateSetting(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<NotificationCategoryUpdateRequestDto> updateReqDtos
    ) {
        notificationSettingService.updateSetting(userDetails.getId(), updateReqDtos);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
