package com.example.Tokkit_server.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

	private final JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("teamtokkit@gmail.com");  // yml에 설정한 이메일
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
		} catch (Exception e) {
			log.error("이메일 전송 실패: {}", e.getMessage());
		}
	}
}
