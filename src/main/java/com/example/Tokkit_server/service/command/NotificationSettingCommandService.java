package com.example.Tokkit_server.service.command;

import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.dto.NotificationSettingDto;

public interface NotificationSettingCommandService {
	NotificationSettingDto getSetting(Long userId);

	void updateSetting(Long userId, NotificationSettingDto dto);

	void createDefaultSetting(User user);
}
