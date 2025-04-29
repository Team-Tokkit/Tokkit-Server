package com.example.Tokkit_server.service.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.Enum.NotificationTemplate;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.NotificationCategoryUpdateReqDto;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.service.EmailNotificationService;
import com.example.Tokkit_server.service.NotificationContentFormatter;
import com.example.Tokkit_server.service.SseNotificationService;
import com.example.Tokkit_server.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
class NotificationCommandServiceImpl implements NotificationCommandService {
	private final NotificationRepository notificationRepository;
	private final NotificationSettingRepository notificationSettingRepository;
	private final UserRepository userRepository;
	private final SseEmitters sseEmitters;
	private final EmailNotificationService emailNotificationService; // 이메일 전송 전용 서비스
	private final SseNotificationService sseNotificationService;
	private final NotificationContentFormatter formatter; // 템플릿에서 제목/내용 추출 헬퍼

	private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000;

	@Transactional
	public void sendNotification(User user, NotificationTemplate template, Object... args) {
		// 제목과 내용 생성
		String title = template.getTitle();
		String content = String.format(template.getContentTemplate(), args);

		// Notification 생성 및 저장
		Notification notification = Notification.builder()
			.user(user)
			.category(template.getCategory())
			.title(title)
			.content(content)
			.build();

		notificationRepository.save(notification);

		// SSE 전송
		sseNotificationService.sendSse(user.getId(), title, content);

		// 이메일 설정 확인
		boolean isEmailEnabled = notificationSettingRepository
			.findByUserAndCategory(user, template.getCategory())
			.isEnabled();

		if (isEmailEnabled) {
			emailNotificationService.sendEmail(user.getEmail(), title, content);
		}
	}


	@Transactional
	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		sseEmitters.add(userId, emitter);

		emitter.onCompletion(() -> sseEmitters.remove(userId));
		emitter.onTimeout(() -> sseEmitters.remove(userId));
		emitter.onError((e) -> sseEmitters.remove(userId));

		log.info("SSE 구독 시작: userId={}", userId);
		return emitter;
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

	@Transactional
	@Override
	public void sendUnsentNotifications(User user) {
		// 1. 보내지 않은 알림 가져오기
		List<Notification> unsentNotifications = notificationRepository.findByUserAndSentFalseAndDeletedFalse(user);

		for (Notification notification : unsentNotifications) {
			// 2. SSE로 알림 전송
			sseNotificationService.sendSse(user.getId(), notification.getTitle(), notification.getContent());

			// 3. 이메일 전송 여부 확인
			NotificationCategorySetting setting = notificationSettingRepository.findByUserAndCategory(user, notification.getCategory());
			if (setting != null && setting.isEnabled()) {
				emailNotificationService.sendEmail(user.getEmail(), notification.getTitle(), notification.getContent());
			}

			// 4. 알림을 보낸 것으로 표시
			notification.markAsSent();
		}

		notificationRepository.saveAll(unsentNotifications);
	}
}