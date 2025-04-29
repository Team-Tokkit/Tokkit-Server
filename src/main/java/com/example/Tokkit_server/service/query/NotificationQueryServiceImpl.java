package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.dto.response.NotificationResDto;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class NotificationQueryServiceImpl implements NotificationQueryService {
	private final NotificationRepository notificationRepository;
	private final NotificationSettingRepository notificationSettingRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
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

	@Transactional(readOnly = true)
	public List<NotificationResDto> getNotificationsByCategory(User user, NotificationCategory category) {
		NotificationCategorySetting setting = notificationSettingRepository.findByUserAndCategory(user, category);
		if (setting == null || !setting.isEnabled()) {
			throw new GeneralException(ErrorStatus._FORBIDDEN);
		}

		return notificationRepository.findByUserAndCategoryAndDeletedFalse(user, category)
			.stream()
			.map(NotificationResDto::from)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<NotificationCategorySettingResDto> getSettings(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		List<NotificationCategorySetting> settings = notificationSettingRepository.findByUser(user);

		return settings.stream()
			.map(NotificationCategorySettingResDto::from)
			.collect(Collectors.toList());
	}
}