package com.example.Tokkit_server.notification.repository;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantNotificationSettingRepository extends JpaRepository<MerchantNotificationCategorySetting, Long> {
    List<MerchantNotificationCategorySetting> findByMerchant(Merchant merchant);
    MerchantNotificationCategorySetting findByMerchantAndCategory(Merchant merchant, NotificationCategory category);

    List<MerchantNotificationCategorySetting> findByMerchantAndEnabledTrue(Merchant merchant);
}
