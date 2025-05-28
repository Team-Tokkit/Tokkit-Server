package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.user.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseEmitters sseEmitters;

    public void sendSse(Long userId, String title, String content) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            try {
                log.info("[SSE] 유저 {}에게 알림 전송 시도", userId); // 로그 추가
                String json = String.format("{\"title\": \"%s\", \"content\": \"%s\"}", title, content);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(json, MediaType.APPLICATION_JSON));
                log.info("[SSE] 유저 {}에게 알림 전송 성공", userId);
            } catch (IOException e) {
                sseEmitters.remove(userId);
                log.error("[SSE] 전송 실패 - emitter 제거됨: {}", e.getMessage());
            }
        } else {
            log.warn("[SSE] emitter 없음 → 전송 실패 (userId: {})", userId); // 로그 추가
        }
    }
}