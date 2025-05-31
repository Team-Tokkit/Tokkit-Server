package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.notification.dto.request.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.entity.Notification;
import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.entity.NotificationContentFormatter;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.enums.NotificationTemplate;
import com.example.Tokkit_server.notification.repository.NotificationRepository;
import com.example.Tokkit_server.notification.repository.NotificationSettingRepository;
import com.example.Tokkit_server.user.dto.response.NotificationCategorySettingResponseDto;
import com.example.Tokkit_server.user.dto.response.NotificationResponseDto;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.user.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;
    private final SseEmitters sseEmitters;
    private final EmailNotificationService emailNotificationService; // 이메일 전송 전용 서비스
    private final SseNotificationService sseNotificationService;

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

        if (template.isSendSse()) {
            boolean success = sseNotificationService.sendSse(user.getId(), title, content);
            if (success) notification.markAsSentSse();
        }

        if (template.isSendEmail()) {
            boolean isEmailEnabled = notificationSettingRepository
                    .findByUserAndCategory(user, template.getCategory())
                    .isEnabled();
            if (isEmailEnabled) {
                boolean success = emailNotificationService.sendEmail(user.getEmail(), title, content);
                if (success) notification.markAsSentMail();
            }
        }

        notificationRepository.save(notification);
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitters.add(userId, emitter);
        log.info("[SSE] 유저 {} 구독 등록됨", userId);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결이 완료되었습니다."));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitters.remove(userId);
        }

        // 트랜잭션 점유 방지를 위해 비동기로 분리된 메서드 호출
        userRepository.findById(userId).ifPresent(this::sendUnsentNotificationsAsync);

        // Ping 유지
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data("keep-alive"));
            } catch (Exception e) {
                emitter.complete();
            }
        }, 30, 30, TimeUnit.SECONDS);

        emitter.onCompletion(() -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
        });

        return emitter;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendUnsentNotificationsAsync(User user) {
        sendUnsentNotifications(user);
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
    @Override
    public void sendUnsentNotifications(User user) {
        List<Notification> unsentNotifications = notificationRepository.findByUserAndDeletedFalse(user);

        for (Notification notification : unsentNotifications) {

            // 1. SSE 전송 (아직 안보냈고 카테고리 설정이 SYSTEM 또는 토큰 등 SSE 대상이라면)
            if (!notification.isSentSse()) {
                boolean success = sseNotificationService.sendSse(user.getId(), notification.getTitle(), notification.getContent());
                if (success) notification.markAsSentSse();
            }

            // 2. 이메일 전송 (아직 안보냈고 사용자가 해당 카테고리 이메일 수신 동의한 경우)
            if (!notification.isSentMail()) {
                NotificationCategorySetting setting = notificationSettingRepository
                        .findByUserAndCategory(user, notification.getCategory());

                if (setting != null && setting.isEnabled()) {
                    boolean success = emailNotificationService.sendEmail(
                            user.getEmail(),
                            notification.getTitle(),
                            notification.getContent()
                    );
                    if (success) notification.markAsSentMail();
                }
            }
        }

        notificationRepository.saveAll(unsentNotifications);
    }


    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications(User user) {
        List<NotificationCategory> enabledCategories = notificationSettingRepository.findByUserAndEnabledTrue(user)
                .stream()
                .map(NotificationCategorySetting::getCategory)
                .collect(Collectors.toList());

        return notificationRepository.findByUserAndCategoriesAndDeletedFalse(user, enabledCategories)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByCategory(User user, NotificationCategory category) {
        NotificationCategorySetting setting = notificationSettingRepository.findByUserAndCategory(user, category);
        if (setting == null || !setting.isEnabled()) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        return notificationRepository.findByUserAndCategoryAndDeletedFalse(user, category)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }
}
