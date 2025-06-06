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
        String title = template.getTitle();
        String content = String.format(template.getContentTemplate(), args);
        log.info("[SSE] sendNotification 호출됨 - userId={}, title={}", user.getId(), title);

        Notification notification = Notification.builder()
                .user(user)
                .category(template.getCategory())
                .title(title)
                .content(content)
                .build();

        notificationRepository.save(notification);

        // SSE 전송
        if (template.isSendSse()) {
            log.info("[NOTI] SSE 전송 시도 - userId={}, title={}", user.getId(), title);
            boolean success = sseNotificationService.sendSse(user.getId(), title, content);
            if (success) {
                log.info("[NOTI] SSE 전송 성공 - userId={}, title={}", user.getId(), title);
                notification.markAsSentSse();
            } else {
                log.warn("[NOTI] SSE 전송 실패 - userId={}, emitter 없음 또는 전송 실패", user.getId());
            }
        } else {
            log.info("[NOTI] 템플릿 설정상 SSE 미전송 - template={}", template.name());
        }

        // 이메일 전송
        if (template.isSendEmail()) {
            boolean isEmailEnabled = notificationSettingRepository
                    .findByUserAndCategory(user, template.getCategory())
                    .isEnabled();

            if (isEmailEnabled) {
                boolean success = emailNotificationService.sendEmail(user.getEmail(), title, content);
                if (success) {
                    log.info("[NOTI] 이메일 전송 성공 - userId={}, email={}", user.getId(), user.getEmail());
                    notification.markAsSentMail();
                } else {
                    log.warn("[NOTI] 이메일 전송 실패 - userId={}, email={}", user.getId(), user.getEmail());
                }
            } else {
                log.info("[NOTI] 유저가 해당 카테고리 이메일 수신 거부 - userId={}, category={}", user.getId(), template.getCategory());
            }
        }

        // 최종 저장
        notificationRepository.save(notification);
    }

    public SseEmitter subscribe(Long userId) {
        // 1. 기존 emitter가 있으면 제거
        SseEmitter previous = sseEmitters.get(userId);
        if (previous != null) {
            previous.complete();
            sseEmitters.remove(userId);
            log.info("[SSE] 기존 emitter 제거됨 - userId={}", userId);
        }

        // 2. 새 emitter 등록
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitters.add(userId, emitter);

        log.info("[SSE] 유저 {} 구독 등록됨", userId);
        log.info("[SSE] 현재 emitter 등록 수: {}", sseEmitters.size());
        log.info("[SSE] 현재 등록된 emitter key 목록: {}", sseEmitters.keySet());

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결이 완료되었습니다."));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitters.remove(userId);
            log.warn("[SSE] connect 전송 실패로 emitter 제거됨 - userId={}", userId);
        }

        userRepository.findById(userId).ifPresent(this::sendUnsentNotificationsAsync);

        // ping 유지용 스케줄러
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data("keep-alive"));
            } catch (Exception e) {
                log.warn("[SSE] ping 전송 실패 - emitter 제거됨: userId={}, error={}", userId, e.getMessage());
                emitter.complete();
                sseEmitters.remove(userId);
                scheduler.shutdown();
            }
        }, 30, 30, TimeUnit.SECONDS);

        emitter.onCompletion(() -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
            log.info("[SSE] emitter 완료 처리됨 - userId={}", userId);
        });

        emitter.onTimeout(() -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
            log.warn("[SSE] emitter 타임아웃 - userId={}", userId);
        });

        emitter.onError((e) -> {
            sseEmitters.remove(userId);
            scheduler.shutdown();
            log.error("[SSE] emitter 에러 발생 - userId={}, error={}", userId, e.getMessage());
        });

        return emitter;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendUnsentNotificationsAsync(User user) {
        try {
            sendUnsentNotifications(user);
        } catch (Exception e) {
            log.error("[SSE] 미발송 알림 전송 실패: userId={}, error={}", user.getId(), e.getMessage());
        }
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
        log.info("[NOTI] 유저 {}의 미전송 알림 {}건 조회됨", user.getId(), unsentNotifications.size());

        for (Notification notification : unsentNotifications) {
            log.debug("[NOTI] 알림 id={}, title={}, sentSse={}, sentMail={}",
                    notification.getId(),
                    notification.getTitle(),
                    notification.isSentSse(),
                    notification.isSentMail());

            // 1. SSE 전송
            if (!notification.isSentSse()) {
                boolean success = sseNotificationService.sendSse(user.getId(), notification.getTitle(), notification.getContent());
                if (success) {
                    log.info("[NOTI] SSE 전송 성공 - 알림 id={}, userId={}", notification.getId(), user.getId());
                    notification.markAsSentSse();
                } else {
                    log.warn("[NOTI] SSE 전송 실패 - 알림 id={}, userId={}", notification.getId(), user.getId());
                }
            } else {
                log.debug("[NOTI] SSE 이미 전송됨 - 알림 id={}, userId={}", notification.getId(), user.getId());
            }

            // 2. 이메일 전송
            if (!notification.isSentMail()) {
                NotificationCategorySetting setting = notificationSettingRepository
                        .findByUserAndCategory(user, notification.getCategory());

                if (setting != null && setting.isEnabled()) {
                    boolean success = emailNotificationService.sendEmail(
                            user.getEmail(),
                            notification.getTitle(),
                            notification.getContent()
                    );
                    if (success) {
                        log.info("[NOTI] 이메일 전송 성공 - 알림 id={}, userId={}, email={}", notification.getId(), user.getId(), user.getEmail());
                        notification.markAsSentMail();
                    } else {
                        log.warn("[NOTI] 이메일 전송 실패 - 알림 id={}, userId={}, email={}", notification.getId(), user.getId(), user.getEmail());
                    }
                } else {
                    log.info("[NOTI] 이메일 수신 설정 안됨 - 알림 id={}, category={}, userId={}", notification.getId(), notification.getCategory(), user.getId());
                }
            } else {
                log.debug("[NOTI] 이메일 이미 전송됨 - 알림 id={}, userId={}", notification.getId(), user.getId());
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
