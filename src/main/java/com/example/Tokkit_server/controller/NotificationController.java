package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.auth.CustomUserDetails;
import com.example.Tokkit_server.dto.response.NotificationResDto;
import com.example.Tokkit_server.service.command.NotificationCommandService;
import com.example.Tokkit_server.service.query.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationQueryService notificationQueryService;
	private final NotificationCommandService notificationCommandService;

	@GetMapping
	public ApiResponse<List<NotificationResDto>> getAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ApiResponse.onSuccess(notificationQueryService.getAllNotifications(userDetails.toUser()));
	}

	@GetMapping("/category")
	public ApiResponse<List<NotificationResDto>> getNotificationsByCategory(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam NotificationCategory category) {
		return ApiResponse.onSuccess(notificationQueryService.getNotificationsByCategory(userDetails.toUser(), category));
	}

	@DeleteMapping("/{notificationId}")
	public ApiResponse<?> deleteNotification(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long notificationId) {
		notificationCommandService.deleteNotification(notificationId, userDetails.toUser());
		return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_DELETE_OK);
	}

	@GetMapping(value = "/subscribe", produces = "text/event-stream;charset=UTF-8")
	public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return notificationCommandService.subscribe(userDetails.getId());
	}

	@PostMapping("/send")
	public ApiResponse<?> sendUnsentNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
		notificationCommandService.sendUnsentNotifications(userDetails.toUser());
		return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_OK);
	}
}
