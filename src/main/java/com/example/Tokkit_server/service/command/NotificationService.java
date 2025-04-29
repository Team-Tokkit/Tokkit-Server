package com.example.Tokkit_server.service.command;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
	SseEmitter subscribe(Long userId);
}
