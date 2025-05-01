package com.example.Tokkit_server.dto.response;

import java.time.LocalDateTime;

import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.Enum.NotificationCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResDto {
	private Long id;
	private String title;
	private String content;
	private NotificationCategory category;
	private LocalDateTime createdAt;

	public static NotificationResDto from(Notification notification) {
		return NotificationResDto.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.content(notification.getContent())
			.category(notification.getCategory())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
