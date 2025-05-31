package com.example.Tokkit_server.notification.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.notification.dto.request.MerchantNotificationCategoryUpdateRequestDto;import com.example.Tokkit_server.notification.dto.response.MerchantNotificationCategorySettingResponseDto;
import com.example.Tokkit_server.notification.service.MerchantNotificationSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants/notifications/setting")
@RequiredArgsConstructor
@Tag(name = "Merchant Notification Setting", description = "가맹점주 알림 설정 관련 API입니다.")
public class MerchantNotificationSettingController {
    private final MerchantNotificationSettingService notificationSettingService;

    @GetMapping
    @Operation(summary = "알림 설정 상태 조회", description = "유저의 알림 카테고리 설정 목록을 조회합니다.")
    public ApiResponse<List<MerchantNotificationCategorySettingResponseDto>> getSettings(@AuthenticationPrincipal CustomMerchantDetails merchantDetails) {
        return ApiResponse.onSuccess(notificationSettingService.getSettings(merchantDetails.getId()));
    }

    @PutMapping
    @Operation(summary = "알림 설정 상태 수정", description = "유저의 알림 카테고리 설정을 수정합니다.")
    public ApiResponse<?> updateSetting(
            @AuthenticationPrincipal CustomMerchantDetails merchantDetails,
            @RequestBody List<MerchantNotificationCategoryUpdateRequestDto> updateReqDtos
    ) {
        notificationSettingService.updateSetting(merchantDetails.getId(), updateReqDtos);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
