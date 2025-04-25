package com.example.Tokkit_server.service.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.NotificationSetting;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.dto.NotificationSettingDto;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingCommandServiceImpl implements NotificationSettingCommandService {

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final NotificationSettingRepository settingRepository;

	public NotificationSettingDto getSetting(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));
		NotificationSetting setting = settingRepository.findByUser(user)
			.orElseThrow(() -> new RuntimeException("설정 없음"));
		return NotificationSettingDto.builder()
			.system(setting.isNotificationSystem())
			.payment(setting.isNotificationPayment())
			.voucher(setting.isNotificationVoucher())
			.token(setting.isNotificationToken())
			.build();
	}

	public void updateSetting(Long userId, NotificationSettingDto dto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 없음"));
		NotificationSetting setting = settingRepository.findByUser(user)
			.orElseThrow(() -> new RuntimeException("설정 없음"));
		setting.updateSettings(dto.isSystem(), dto.isPayment(), dto.isVoucher(), dto.isToken());
	}

	public void createDefaultSetting(User user) {
		NotificationSetting setting = NotificationSetting.builder()
			.user(user)
			.build(); // true 기본값 사용
		settingRepository.save(setting);
	}

}
