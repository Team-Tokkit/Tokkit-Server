package com.example.Tokkit_server.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.auth.CustomUserDetails;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;
import com.example.Tokkit_server.service.command.NotificationCommandService;
import com.example.Tokkit_server.service.command.NotificationSettingCommandService;
import com.example.Tokkit_server.service.query.NotificationQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications/setting")
@RequiredArgsConstructor
@Tag(name = "NotificationSetting", description = "알림 설정 관련 API입니다.")
public class NotificationSettingController {

	private final NotificationSettingCommandService notificationSettingCommandService;

	@GetMapping
	@Operation(summary = "알림 설정 상태 조회", description = "유저의 알림 카테고리 설정 목록을 조회합니다.")
	public ApiResponse<List<NotificationCategorySettingResDto>> getSettings(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ApiResponse.onSuccess(notificationSettingCommandService.getSettings(userDetails.getId()));
	}

	@PutMapping
	@Operation(summary = "알림 설정 상태 수정", description = "유저의 알림 카테고리 설정을 수정합니다.")
	public ApiResponse<?> updateSetting(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody List<NotificationCategoryUpdateReqDto> updateReqDtos
	) {
		notificationSettingCommandService.updateSetting(userDetails.getId(), updateReqDtos);
		return ApiResponse.onSuccess(SuccessStatus._OK);
	}
}

