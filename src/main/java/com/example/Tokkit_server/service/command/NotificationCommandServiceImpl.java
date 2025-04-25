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
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		NotificationSetting setting = settingRepository.findByUser(user)
			.orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));

		List<NotificationCategory> allowedCategories = getAllowedCategories(setting);

		if (categories != null && !categories.isEmpty()) {
			allowedCategories.retainAll(categories);
		}

		List<Notification> notifications = allowedCategories.isEmpty()
			? List.of()
			: notificationRepository.findByUserIdAndCategoryInAndIsDeletedFalseOrderByCreatedAtDesc(userId, allowedCategories);

		return notifications.stream()
			.map(NotificationResDto::from)
			.toList();
	}

	private List<NotificationCategory> getAllowedCategories(NotificationSetting setting) {
		List<NotificationCategory> allowed = new java.util.ArrayList<>();

		if (setting.isNotificationSystem()) {
			allowed.add(NotificationCategory.SYSTEM);
		}
		if (setting.isNotificationPayment()) {
			allowed.add(NotificationCategory.PAYMENT);
		}
		if (setting.isNotificationVoucher()) {
			allowed.add(NotificationCategory.VOUCHER);
		}
		if (setting.isNotificationToken()) {
			allowed.add(NotificationCategory.TOKEN);
		}
		return allowed;
	}


	@Override
	public void deleteNotificationByNotificationIdAndUserId(Long userId, Long id) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		Notification notification = notificationRepository
			.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

		notificationRepository.deleteById(id);
	}
}
