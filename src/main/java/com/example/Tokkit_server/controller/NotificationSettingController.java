package com.example.Tokkit_server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;
import com.example.Tokkit_server.service.command.NotificationSettingCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications/setting")
@RequiredArgsConstructor
public class NotificationSettingController {

	private final NotificationSettingCommandService settingCommandService;

	@GetMapping("/{userId}")
	public ApiResponse<List<NotificationCategorySettingResDto>> getSettings(@PathVariable Long userId) {
		return ApiResponse.onSuccess(settingCommandService.getSettings(userId));
	}

	@PutMapping("/{userId}")
	public ApiResponse<?> updateSetting(
		@PathVariable Long userId,
		@RequestBody List<NotificationCategoryUpdateReqDto> updateReqDtos
		) {
		settingCommandService.updateSetting(userId, updateReqDtos);
		return ApiResponse.onSuccess(SuccessStatus._OK);
	}
}
