package com.example.Tokkit_server.service.command;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationSetting;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.dto.NotificationResDto;
import com.example.Tokkit_server.dto.NotificationSettingDto;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final NotificationRepository notificationRepository;

	@Autowired
	private final NotificationSettingRepository settingRepository;

	@Override
	public List<NotificationResDto> getNotifications(Long userId, List<NotificationCategory> categories) {
		List<Notification> notifications = (categories == null || categories.isEmpty())
			? notificationRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
			: notificationRepository.findByUserIdAndCategoryInAndIsDeletedFalseOrderByCreatedAtDesc(userId, categories);

		return notifications.stream()
			.map(NotificationResDto::from)
			.toList();
	}

	@Override
	public void deleteNotificationByNotificationIdAndUserId(Long userId, Long id) {
		Notification notification = notificationRepository
			.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

		notificationRepository.deleteById(id);
	}
}
