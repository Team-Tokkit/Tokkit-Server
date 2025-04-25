package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.NotificationSettingDto;
import com.example.Tokkit_server.service.command.NotificationSettingCommandService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/setting")
public class NotificationSettingController {

	private final NotificationSettingCommandService settingService;

	@GetMapping
	public ApiResponse<NotificationSettingDto> getSetting(@RequestParam Long userId) {
		return ApiResponse.onSuccess(settingService.getSetting(userId));
	}

	@PutMapping
	public ApiResponse<?> updateSetting(@RequestParam Long userId, @RequestBody NotificationSettingDto dto) {
		settingService.updateSetting(userId, dto);
		return ApiResponse.onSuccess(null);
	}
}
