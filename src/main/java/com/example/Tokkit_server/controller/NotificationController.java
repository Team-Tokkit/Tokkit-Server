package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationResDto;
import com.example.Tokkit_server.service.command.NotificationCommandService;
import com.example.Tokkit_server.service.command.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;
	private final NotificationCommandService notificationCommandService;

	// 알림 전체 조회
	@GetMapping
	public ApiResponse<List<NotificationResDto>> getAllNotifications(@RequestAttribute("user") User user) {
		return ApiResponse.onSuccess(notificationCommandService.getAllNotifications(user));
	}

	// 카테고리별 알림 조회
	@GetMapping("/category")
	public ApiResponse<List<NotificationResDto>> getNotificationsByCategory(
		@RequestAttribute("user") User user,
		@RequestParam NotificationCategory category) {

		return ApiResponse.onSuccess(notificationCommandService.getNotificationsByCategory(user, category));
	}

	// 알림 삭제
	@DeleteMapping("/{notificationId}")
	public ApiResponse<?> deleteNotification(
		@RequestAttribute("user") User user,
		@PathVariable Long notificationId) {

		notificationCommandService.deleteNotification(notificationId, user);
		return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_DELETE_OK);
	}

	// SSE 구독
	@GetMapping(value = "/subscribe", produces = "text/event-stream;charset=UTF-8")
	public SseEmitter subscribe(@RequestAttribute("user") User user) {
		return notificationCommandService.subscribe(user.getId());
	}

	// 알림 발송 (이메일 + SSE)
	@PostMapping("/send")
	public ApiResponse<?> sendUnsentNotifications(@RequestAttribute("user") User user) {
		notificationService.subscribe(user.getId());
		return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_OK);
	}
}
