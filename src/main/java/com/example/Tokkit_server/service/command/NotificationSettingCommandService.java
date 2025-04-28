package com.example.Tokkit_server.service.command;

import java.util.List;

import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;

public interface NotificationSettingCommandService {

	List<NotificationCategorySettingResDto> getSettings(Long userId);

	void updateSetting(Long userId, List<NotificationCategoryUpdateReqDto> updateReqDtos);

	// void updateAllSettings(Long userId, boolean enabled);
}
