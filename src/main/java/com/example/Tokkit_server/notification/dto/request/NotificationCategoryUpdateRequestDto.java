package com.example.Tokkit_server.notification.dto.request;


import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.user.entity.User;
import lombok.Getter;

@Getter
public class NotificationCategoryUpdateRequestDto {
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