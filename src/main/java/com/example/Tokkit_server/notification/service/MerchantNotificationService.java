package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.notification.dto.response.MerchantNotificationResponseDto;
import com.example.Tokkit_server.notification.entity.MerchantNotification;
import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.enums.NotificationTemplate;
import com.example.Tokkit_server.notification.repository.MerchantNotificationRepository;
import com.example.Tokkit_server.notification.repository.MerchantNotificationSettingRepository;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantNotificationService {
    private final MerchantNotificationRepository notificationRepository;
    private final MerchantNotificationSettingRepository settingRepository;
    private final MerchantRepository merchantRepository;
    private final SseEmitters sseEmitters;
    private final EmailNotificationService emailNotificationService;
    private final SseNotificationService sseNotificationService;

    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000;

    @Transactional
    public void sendMerchantNotification(Merchant merchant, NotificationTemplate template, Object... args) {
        String title = template.getTitle();
        String content = String.format(template.getContentTemplate(), args);

        MerchantNotification notification = MerchantNotification.builder()
                .merchant(merchant)
                .category(template.getCategory())
                .title(title)
                .content(content)
                .build();

        notificationRepository.save(notification);
    }

    public SseEmitter subscribe(Long merchantId){
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitters.add(merchantId, emitter);
        log.info("[SSE] 가맹점주 {} 구독 등록됨", merchantId);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결이 완료되었습니다."));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitters.remove(merchantId);
        }

        merchantRepository.findById(merchantId).ifPresent(this::sendUnsentMerchantNotificationsAsync);

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
            sseEmitters.remove(merchantId);
            scheduler.shutdown();
        });

        emitter.onTimeout(() -> {
            sseEmitters.remove(merchantId);
            scheduler.shutdown();
        });

        emitter.onError((e) -> {
            sseEmitters.remove(merchantId);
            scheduler.shutdown();
        });

        return emitter;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendUnsentMerchantNotificationsAsync(Merchant merchant) {
        sendUnsentMerchantNotifications(merchant);
    }

    @Transactional
    public void deleteNotification(Long merchantNotificationId, Merchant merchant) {
        MerchantNotification merchantNotification = notificationRepository.findByIdAndMerchant(merchantNotificationId, merchant)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if (!merchantNotification.getMerchant().getId().equals(merchant.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        merchantNotification.softDelete();;
        notificationRepository.save(merchantNotification);
    }

    @Transactional
    public void sendUnsentMerchantNotifications(Merchant merchant) {
        List<MerchantNotification> unsentNotifications = notificationRepository.findByMerchantAndDeletedFalse(merchant);

        for (MerchantNotification notification : unsentNotifications) {

            if (!notification.isSentSse()) {
                boolean success = sseNotificationService.sendSse(merchant.getId(), notification.getTitle(), notification.getContent());
                if (success) notification.markAsSentSse();
            }
            if (!notification.isSentMail()) {
                MerchantNotificationCategorySetting setting = settingRepository
                        .findByMerchantAndCategory(merchant, notification.getCategory());

                if (setting != null && setting.isEnabled()) {
                    boolean success = emailNotificationService.sendEmail(
                            merchant.getEmail(),
                            notification.getTitle(),
                            notification.getContent()
                    );
                    if (success) notification.markAsSentMail();;
                }
            }
        }

        notificationRepository.saveAll(unsentNotifications);
    }

    @Transactional(readOnly = true)
    public List<MerchantNotificationResponseDto> getAllNotifications(Merchant merchant) {
        List<NotificationCategory> enabledCategories = settingRepository.findByMerchantAndEnabledTrue(merchant)
                .stream()
                .map(MerchantNotificationCategorySetting::getCategory)
                .collect(Collectors.toList());

        return notificationRepository.findByMerchantAndCategoriesAndDeletedFalse(merchant, enabledCategories)
                .stream()
                .map(MerchantNotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MerchantNotificationResponseDto> getMerchantNotificationsByCategory(Merchant merchant, NotificationCategory category) {
        MerchantNotificationCategorySetting setting = settingRepository.findByMerchantAndCategory(merchant, category);
        if (setting == null || !setting.isEnabled()) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        return notificationRepository.findByMerchantAndCategoryAndDeletedFalse(merchant, category)
                .stream()
                .map(MerchantNotificationResponseDto::from)
                .collect(Collectors.toList());
    }
}
