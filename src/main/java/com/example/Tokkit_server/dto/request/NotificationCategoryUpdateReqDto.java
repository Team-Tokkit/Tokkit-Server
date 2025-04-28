package com.example.Tokkit_server.dto.request;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.User;

import lombok.Getter;

@Getter
public class NotificationCategoryUpdateReqDto {
	private NotificationCategory category;
	private boolean enabled;

	public NotificationCategorySetting toEntity(User user) {
		return NotificationCategorySetting.builder()
			.user(user)
			.category(this.category)
			.enabled(this.enabled)
			.build();
	}
}
