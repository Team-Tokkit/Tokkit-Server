package com.example.Tokkit_server.notification.dto.response;

import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MerchantNotificationCategorySettingResponseDto {
    private NotificationCategory category;
    private boolean enabled;

    public static MerchantNotificationCategorySettingResponseDto from(MerchantNotificationCategorySetting categorySetting) {
        return MerchantNotificationCategorySettingResponseDto.builder()
                .category(categorySetting.getCategory())
                .enabled(categorySetting.isEnabled())
                .build();
    }
}
