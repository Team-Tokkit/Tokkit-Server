package com.example.Tokkit_server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.dto.NotificationResDto;
import com.example.Tokkit_server.service.UserService;
import com.example.Tokkit_server.service.command.NotificationCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final UserService userService;

	private final NotificationCommandService notificationCommandService;

	@GetMapping
	public ApiResponse<List<NotificationResDto>> getNotifications(
		@RequestParam Long userId,
		@RequestParam(required = false) List<NotificationCategory> category
	) {
		List<NotificationResDto> notifications = notificationCommandService.getNotifications(userId, category);
		return ApiResponse.onSuccess(notifications);
	}

	@DeleteMapping()
	public ApiResponse<?> deleteNotification(
		@RequestParam Long userId,
		@RequestParam Long notificationId
	) {
		notificationCommandService.deleteNotificationByNotificationIdAndUserId(userId, notificationId);
		return ApiResponse.onSuccess(null);
	}
}
