package com.example.Tokkit_server.service.command;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Tokkit_server.Enum.NotificationTemplate;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;

public interface NotificationCommandService {
	void sendNotification(User user, NotificationTemplate template, Object... args);
	SseEmitter subscribe(Long userId);
	void deleteNotification(Long notificationId, User user);
	void updateSetting(Long userId, List<NotificationCategoryUpdateReqDto> updateReqDtos);

	void sendUnsentNotifications(User user);
}