package com.example.Tokkit_server.service.command;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;
import com.example.Tokkit_server.dto.response.NotificationCategorySettingResDto;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationSettingCommandServiceImpl implements NotificationSettingCommandService {

	private final NotificationSettingRepository notificationSettingRepository;
	private final UserRepository userRepository;

	@Override
	public List<NotificationCategorySettingResDto> getSettings(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		List<NotificationCategorySetting> settings = notificationSettingRepository.findByUser(user);
		return settings.stream()
			.map(NotificationCategorySettingResDto::from)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateSetting(Long userId, List<NotificationCategoryUpdateReqDto> updateReqDtos) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		List<NotificationCategorySetting> settings = notificationSettingRepository.findByUser(user);
		if (settings.isEmpty()) {
			throw new GeneralException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND);
		}

		Map<NotificationCategory, Boolean> updateMap = updateReqDtos.stream()
			.collect(Collectors.toMap(NotificationCategoryUpdateReqDto::getCategory, NotificationCategoryUpdateReqDto::isEnabled));

		for (NotificationCategorySetting setting : settings) {
			if (updateMap.containsKey(setting.getCategory())) {
				setting.update(updateMap.get(setting.getCategory()));
			}
		}
	}
}
