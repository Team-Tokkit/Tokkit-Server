package com.example.Tokkit_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.service.command.EmailNotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailNotificationController {

	private final EmailNotificationService emailNotificationService;

	@PostMapping("/send-notifications/{userId}")
	public String sendUnsentNotifications(@PathVariable Long userId) {
		emailNotificationService.sendUnsentNotifications(userId);
		return "알림 메일 발송 완료";
	}
}
