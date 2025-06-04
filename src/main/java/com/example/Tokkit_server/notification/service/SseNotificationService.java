package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.user.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseEmitters sseEmitters;

    public boolean sendSse(Long userId, String title, String content) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter == null) {
            log.warn("❌ emitter 없음 - userId={}", userId);
            return false;
        }

        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("notification")
                    .data(Map.of("title", title, "content", content)) // 프론트와 호환
                    .id(UUID.randomUUID().toString());

            emitter.send(event);
            return true;

        } catch (IOException e) {
            log.warn("❌ SSE 전송 실패 - userId={}, error={}", userId, e.getMessage());
            emitter.completeWithError(e);
            sseEmitters.remove(userId);
            return false;
        }
    }
}