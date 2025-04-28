package com.example.Tokkit_server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationResDto;
import com.example.Tokkit_server.service.command.NotificationCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationCommandService notificationCommandService;

	@GetMapping
	public ApiResponse<List<NotificationResDto>> getAllNotifications(@RequestHeader("user") User user) {
		return ApiResponse.onSuccess(notificationCommandService.getAllNotifications(user));
	}

	@GetMapping("/category")
	public ApiResponse<List<NotificationResDto>> getNotificationsByCategory(@RequestHeader("user") User user, @RequestParam NotificationCategory category) {
		return ApiResponse.onSuccess(notificationCommandService.getNotificationsByCategory(user, category));
	}

	@DeleteMapping("/{notificationId}")
	public ApiResponse<?> deleteNotification(@RequestHeader("user") User user, @PathVariable Long notificationId) {
		notificationCommandService.deleteNotification(notificationId, user);
		return ApiResponse.onSuccess(null);
	}
}
