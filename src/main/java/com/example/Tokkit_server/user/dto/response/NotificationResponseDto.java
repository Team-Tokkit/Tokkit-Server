package com.example.Tokkit_server.user.dto.response;

import java.time.LocalDateTime;


import com.example.Tokkit_server.notification.entity.Notification;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String content;
    private NotificationCategory category;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .category(notification.getCategory())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}