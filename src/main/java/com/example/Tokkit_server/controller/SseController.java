package com.example.Tokkit_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Tokkit_server.util.SseEmitters;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

	private final SseEmitters sseEmitters;

	@GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream;charset=UTF-8")
	public SseEmitter subscribe(@PathVariable Long userId) {
		SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
		sseEmitters.add(userId, emitter);

		emitter.onCompletion(() -> sseEmitters.remove(userId));
		emitter.onTimeout(() -> sseEmitters.remove(userId));
		emitter.onError((e) -> sseEmitters.remove(userId));

		return emitter;
	}
}
