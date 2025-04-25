package com.example.Tokkit_server.service.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.dto.NotificationResDto;
import com.example.Tokkit_server.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	@Autowired
	private final NotificationRepository notificationRepository;

	@Override
	public List<NotificationResDto> getNotifications(Long userId, List<NotificationCategory> categories) {
		List<Notification> notifications = (categories == null || categories.isEmpty())
			? notificationRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
			: notificationRepository.findByUserIdAndCategoryInAndIsDeletedFalseOrderByCreatedAtDesc(userId, categories);

		return notifications.stream()
			.map(NotificationResDto::from)
			.toList();
	}

}
