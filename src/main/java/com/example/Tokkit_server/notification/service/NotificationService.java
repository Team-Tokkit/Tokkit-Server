package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.notification.dto.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.enums.NotificationTemplate;
import com.example.Tokkit_server.user.dto.response.NotificationCategorySettingResponseDto;
import com.example.Tokkit_server.user.dto.response.NotificationResponseDto;
import com.example.Tokkit_server.user.entity.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
    void sendNotification(User user, NotificationTemplate template, Object... args);
    SseEmitter subscribe(Long userId);
    void deleteNotification(Long notificationId, User user);
    void updateSetting(Long userId, List<NotificationCategoryUpdateRequestDto> updateReqDtos);

    void sendUnsentNotifications(User user);
    List<NotificationResponseDto> getAllNotifications(User user);
    List<NotificationResponseDto> getNotificationsByCategory(User user, NotificationCategory category);
    List<NotificationCategorySettingResponseDto> getSettings(Long userId);
}
