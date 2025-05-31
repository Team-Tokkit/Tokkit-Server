package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.notification.dto.request.MerchantNotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.dto.request.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.dto.response.MerchantNotificationCategorySettingResponseDto;
import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.repository.MerchantNotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantNotificationSettingService {
    private final MerchantNotificationSettingRepository settingRepository;
    private final MerchantRepository merchantRepository;

    public List<MerchantNotificationCategorySettingResponseDto> getSettings(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        List<MerchantNotificationCategorySetting> settings = settingRepository.findByMerchant(merchant);
        return settings.stream()
                .map(MerchantNotificationCategorySettingResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateSetting(Long merchantId, List<MerchantNotificationCategoryUpdateRequestDto> updateRequest) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        List<MerchantNotificationCategorySetting> settings = settingRepository.findByMerchant(merchant);
        if (settings.isEmpty()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND);
        }

        Map<NotificationCategory, Boolean> updateMap = updateRequest.stream()
                .collect(Collectors.toMap(MerchantNotificationCategoryUpdateRequestDto::getCategory, MerchantNotificationCategoryUpdateRequestDto::isEnabled));

        for (MerchantNotificationCategorySetting setting : settings) {
            if (updateMap.containsKey(setting.getCategory())) {
                setting.update(updateMap.get(setting.getCategory()));
            }
        }
    }

}
