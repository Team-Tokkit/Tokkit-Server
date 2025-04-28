package com.example.Tokkit_server.service.command;

import java.io.IOException;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.util.SseEmitters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

	private final JavaMailSender mailSender;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final SseEmitters sseEmitters;

	@Transactional
	public void sendUnsentNotifications(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		List<Notification> notifications = notificationRepository.findByUserAndSentFalseAndDeletedFalse(user);

		if (notifications.isEmpty()) {
			log.info("발송할 알림이 없습니다.");
			return;
		}

		for (Notification notification : notifications) {
			sendEmail(user.getEmail(), notification.getTitle(), notification.getContent());
			sendSse(userId, notification.getTitle(), notification.getContent());
			notification.markAsSent();
		}
	}

	private void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("teamtokkit@gmail.com");  // 너의 yml 이메일
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	private void sendSse(Long userId, String title, String content) {
		SseEmitter emitter = sseEmitters.get(userId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(title + ": " + content));
			} catch (IOException e) {
				sseEmitters.remove(userId);
				log.error("SSE 전송 실패. 연결 제거됨", e);
			}
		}
	}
}
