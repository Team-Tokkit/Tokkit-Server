package com.example.Tokkit_server.service.command;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationResDto;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.NotificationSettingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	private final NotificationRepository notificationRepository;
	private final NotificationSettingRepository notificationSettingRepository;
	private final EmailNotificationService emailService;

	@Transactional
	public List<NotificationResDto> getAllNotifications(User user) {
		List<NotificationCategory> enabledCategories = notificationSettingRepository.findByUserAndEnabledTrue(user)
			.stream()
			.map(NotificationCategorySetting::getCategory)
			.collect(Collectors.toList());

		return notificationRepository.findByUserAndCategoriesAndDeletedFalse(user, enabledCategories)
			.stream()
			.map(NotificationResDto::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public List<NotificationResDto> getNotificationsByCategory(User user, NotificationCategory category) {
		// 카테고리 설정 확인
		NotificationCategorySetting setting = notificationSettingRepository.findByUserAndCategory(user, category);
		if (setting == null || !setting.isEnabled()) {
			throw new GeneralException(ErrorStatus._FORBIDDEN); // 알림 꺼놓은 카테고리 접근 차단
		}

		return notificationRepository.findByUserAndCategoryAndDeletedFalse(user, category)
			.stream()
			.map(NotificationResDto::from)
			.collect(Collectors.toList());
	}


	@Transactional
	public void deleteNotification(Long notificationId, User user) {
		Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
			.orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

		if (!notification.getUser().getId().equals(user.getId())) {
			throw new GeneralException(ErrorStatus._FORBIDDEN);
		}

		notification.softDelete();
		notificationRepository.save(notification);
	}
}
