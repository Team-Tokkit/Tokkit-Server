package com.example.Tokkit_server.service.command;

import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;

import java.util.List;

public interface NotificationSettingCommandService {
	List<NotificationCategorySettingResDto> getSettings(Long userId);
	void updateSetting(Long userId, List<NotificationCategoryUpdateReqDto> updateReqDtos);
}
