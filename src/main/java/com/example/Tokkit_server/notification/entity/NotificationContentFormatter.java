package com.example.Tokkit_server.notification.entity;

import com.example.Tokkit_server.notification.enums.NotificationTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationContentFormatter {

    /**
     * 템플릿과 가변 인자를 받아 알림 내용을 구성한다.
     */
    public String format(NotificationTemplate template, Object... args) {
        try {
            return String.format(template.getContentTemplate(), args);
        } catch (Exception e) {
            log.error("[NotificationContentFormatter] 템플릿 메시지 포맷 실패: {}", e.getMessage());
            return template.getContentTemplate(); // 실패 시 원본 템플릿 반환
        }
    }
}