package com.example.Tokkit_server.service.command;

import java.util.List;

import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.dto.NotificationResDto;
import com.example.Tokkit_server.dto.NotificationSettingDto;

public interface NotificationCommandService {
	List<NotificationResDto> getNotifications(Long userId, List<NotificationCategory> categories);

	void deleteNotificationByNotificationIdAndUserId(Long userId, Long notificationId);
}
