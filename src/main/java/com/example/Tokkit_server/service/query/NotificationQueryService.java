package com.example.Tokkit_server.service.query;

import java.util.List;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.dto.response.NotificationResDto;

public interface NotificationQueryService {
	List<NotificationResDto> getAllNotifications(User user);
	List<NotificationResDto> getNotificationsByCategory(User user, NotificationCategory category);
	List<NotificationCategorySettingResDto> getSettings(Long userId);
}