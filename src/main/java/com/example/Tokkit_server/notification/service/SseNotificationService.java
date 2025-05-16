package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.user.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseEmitters sseEmitters;

    public void sendSse(Long userId, String title, String content) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(title + ": " + content, MediaType.valueOf("text/plain;charset=UTF-8")));
            } catch (IOException e) {
                sseEmitters.remove(userId);
                log.error("[SseNotificationService] SSE 전송 실패 - 연결 제거됨: {}", e.getMessage());
            }
        }
    }
}