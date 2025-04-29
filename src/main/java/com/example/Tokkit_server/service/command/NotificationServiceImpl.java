package com.example.Tokkit_server.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Tokkit_server.utils.SseEmitters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final SseEmitters sseEmitters;

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

	@Transactional
	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		sseEmitters.add(userId, emitter);

		emitter.onCompletion(() -> sseEmitters.remove(userId));
		emitter.onTimeout(() -> sseEmitters.remove(userId));
		emitter.onError((e) -> sseEmitters.remove(userId));

		log.info("SSE 구독 시작: userId={}", userId);
		return emitter;
	}
}
