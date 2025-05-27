package com.example.Tokkit_server.user.dto.response;


import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCategorySettingResponseDto {
    private NotificationCategory category;
    private boolean enabled;

    public static NotificationCategorySettingResponseDto from(NotificationCategorySetting setting) {
        return NotificationCategorySettingResponseDto.builder()
                .category(setting.getCategory())
                .enabled(setting.isEnabled())
                .build();
    }
}