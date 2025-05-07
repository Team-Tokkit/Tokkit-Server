package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.notification.dto.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.repository.NotificationSettingRepository;
import com.example.Tokkit_server.user.dto.response.NotificationCategorySettingResponseDto;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationSettingServiceImpl implements NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationCategorySettingResponseDto> getSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<NotificationCategorySetting> settings = notificationSettingRepository.findByUser(user);
        return settings.stream()
                .map(NotificationCategorySettingResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSetting(Long userId, List<NotificationCategoryUpdateRequestDto> updateReqDtos) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<NotificationCategorySetting> settings = notificationSettingRepository.findByUser(user);
        if (settings.isEmpty()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND);
        }

        Map<NotificationCategory, Boolean> updateMap = updateReqDtos.stream()
                .collect(Collectors.toMap(NotificationCategoryUpdateRequestDto::getCategory, NotificationCategoryUpdateRequestDto::isEnabled));

        for (NotificationCategorySetting setting : settings) {
            if (updateMap.containsKey(setting.getCategory())) {
                setting.update(updateMap.get(setting.getCategory()));
            }
        }
    }
}
