package com.example.Tokkit_server.notification.dto.request;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import lombok.Getter;

@Getter
public class MerchantNotificationCategoryUpdateRequestDto {
    private NotificationCategory category;
    private boolean enabled;
}
