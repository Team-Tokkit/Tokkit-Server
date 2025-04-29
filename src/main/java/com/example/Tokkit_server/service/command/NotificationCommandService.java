package com.example.Tokkit_server.service.command;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationResDto;

public interface NotificationCommandService {

	SseEmitter subscribe(Long userId);

	List<NotificationResDto> getAllNotifications(User user);

	List<NotificationResDto> getNotificationsByCategory(User user, NotificationCategory category);

	void deleteNotification(Long notificationId, User user);
}
