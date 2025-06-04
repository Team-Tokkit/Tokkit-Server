package com.example.Tokkit_server.notification.scheduler;

import com.example.Tokkit_server.notification.repository.MerchantNotificationRepository;
import com.example.Tokkit_server.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;
    private final MerchantNotificationRepository merchantNotificationRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanOldNotifications() {
        LocalDateTime cutoff = LocalDate.now().minusDays(7).atStartOfDay();
        int updatedCount = notificationRepository.softDeleteOldNotifications(cutoff);
        int updatedMerchantCount = merchantNotificationRepository.softDeleteOldNotifications(cutoff);
        log.info("🧹 [유저 알림 정리] {}개의 알림 soft delete 처리 완료", updatedCount);
        log.info("🧹 [가맹점주 알림 정리] {}개의 알림 soft delete 처리 완료", updatedMerchantCount);
    }
}
