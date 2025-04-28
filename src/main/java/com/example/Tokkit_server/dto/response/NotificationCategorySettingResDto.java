package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCategorySettingResDto {
	private NotificationCategory category;
	private boolean enabled;

	public static NotificationCategorySettingResDto from(NotificationCategorySetting setting) {
		return NotificationCategorySettingResDto.builder()
			.category(setting.getCategory())
			.enabled(setting.isEnabled())
			.build();
	}
}
