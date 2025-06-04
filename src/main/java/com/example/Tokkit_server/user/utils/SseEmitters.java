package com.example.Tokkit_server.user.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseEmitters {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void add(Long userId, SseEmitter emitter) {
        SseEmitter oldEmitter = emitters.put(userId, emitter);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.warn("❗ 기존 emitter 종료 중 오류 - userId={}", userId, e);
            }
        }
    }

    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

    public void remove(Long userId) {
        emitters.remove(userId);
    }

    public Map<Long, SseEmitter> getEmitters() {
        return emitters;
    }

    // 현재 emitter 개수 확인용
    public int size() {
        return emitters.size();
    }

    // 현재 등록된 유저 ID 목록 반환
    public Set<Long> keySet() {
        return emitters.keySet();
    }
}