package com.example.Tokkit_server.notification.repository;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.notification.entity.MerchantNotification;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MerchantNotificationRepository extends JpaRepository<MerchantNotification, Long> {
    @Query("SELECT n FROM MerchantNotification n WHERE n.merchant = :merchant AND n.category IN :categories AND n.deleted = false")
    List<MerchantNotification> findByMerchantAndCategoriesAndDeletedFalse(@Param("merchant") Merchant merchant, @Param("categories")List<NotificationCategory> categories);

    @Query("SELECT n FROM MerchantNotification n WHERE n.merchant = :merchant AND n.category = :category AND n.deleted = false")
    List<MerchantNotification> findByMerchantAndCategoryAndDeletedFalse(@Param("merchant") Merchant merchant, @Param("category") NotificationCategory category);

    Optional<MerchantNotification> findByIdAndMerchant(Long id, Merchant merchant);

    List<MerchantNotification> findByMerchantAndDeletedFalse(Merchant merchant);

    @Modifying
    @Query("UPDATE MerchantNotification n SET n.deleted = true WHERE n.deleted = false AND n.createdAt < :cutoff")
    int softDeleteOldNotifications(@org.springframework.data.repository.query.Param("cutoff") LocalDateTime cutoff);
}
