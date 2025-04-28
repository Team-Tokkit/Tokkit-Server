package com.example.Tokkit_server.service.command;

import java.util.List;

import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.dto.response.NotificationResDto;

public interface NotificationCommandService {
	List<NotificationResDto> getAllNotifications(User user);

	List<NotificationResDto> getNotificationsByCategory(User user, NotificationCategory category);

	void deleteNotification(Long notificationId, User user);
}
