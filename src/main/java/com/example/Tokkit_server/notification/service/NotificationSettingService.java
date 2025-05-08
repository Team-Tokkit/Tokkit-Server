package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.notification.dto.request.NotificationCategoryUpdateRequestDto;
import com.example.Tokkit_server.user.dto.response.NotificationCategorySettingResponseDto;

import java.util.List;

public interface NotificationSettingService {
    List<NotificationCategorySettingResponseDto> getSettings(Long userId);
    void updateSetting(Long userId, List<NotificationCategoryUpdateRequestDto> updateReqDtos);
}
