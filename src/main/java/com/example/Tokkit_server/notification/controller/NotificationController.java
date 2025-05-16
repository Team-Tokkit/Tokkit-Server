package com.example.Tokkit_server.notification.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.SuccessStatus;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.service.NotificationService;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.response.NotificationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API입니다.")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 전체 목록 조회", description = "유저가 설정한 카테고리에 맞춰 전체 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDto>> getAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(notificationService.getAllNotifications(userDetails.toUser()));
    }

    @GetMapping("/category")
    @Operation(summary = "알림 카테고리별 목록 조회", description = "유저가 설정한 카테고리에 맞춰 카테고리별 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDto>> getNotificationsByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam NotificationCategory category) {
        return ApiResponse.onSuccess(notificationService.getNotificationsByCategory(userDetails.toUser(), category));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
    public ApiResponse<?> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId, userDetails.toUser());
        return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_DELETE_OK);
    }

    @GetMapping(value = "/subscribe", produces = "text/event-stream;charset=UTF-8")
    @Operation(summary = "알림 구독", description = "유저가 설정한 카테고리에 맞게 SSE 알림을 구독 상태로 만들어줍니다.")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.subscribe(userDetails.getId());
    }

    @PostMapping("/send")
    @Operation(summary = "모든 알림 전송", description = "유저가 설정한 카테고리에 맞게 SSE 알림과 이메일 알림을 보냅니다.")
    public ApiResponse<?> sendUnsentNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.sendUnsentNotifications(userDetails.toUser());
        return ApiResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_OK);
    }
}
