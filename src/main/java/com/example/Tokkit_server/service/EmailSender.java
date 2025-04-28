package com.example.Tokkit_server.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

	private final JavaMailSender emailSender;

	public void sendAuthCodeEmail(String to, String authCode) throws Exception {
		MimeMessage message = emailSender.createMimeMessage();

		message.addRecipients(MimeMessage.RecipientType.TO, to);
		message.setSubject("[Tokkit] 이메일 인증 코드입니다.");

		StringBuilder msgg = new StringBuilder();
		msgg.append("<div style='margin:20px; font-family:Arial,sans-serif;'>");
		msgg.append("<h1 style='color:#4CAF50;'>Tokkit 인증번호</h1>");
		msgg.append("<p>안녕하세요, Tokkit 서비스입니다.</p>");
		msgg.append("<p>아래 인증번호를 입력하여 이메일 인증을 완료해 주세요.</p>");
		msgg.append("<div style='margin:20px auto; padding:20px; border:1px solid #e0e0e0; width:fit-content; text-align:center;'>");
		msgg.append("<span style='font-size:24px; font-weight:bold; color:#333;'>");
		msgg.append(authCode);
		msgg.append("</span>");
		msgg.append("</div>");
		msgg.append("<p style='color:#888;'>인증번호는 5분 동안 유효합니다.</p>");
		msgg.append("<p style='color:#888;'>만약 요청하지 않은 메일이라면 이 메일을 무시해 주세요.</p>");
		msgg.append("<br>");
		msgg.append("<p style='font-size:12px; color:#aaa;'>Tokkit Team</p>");
		msgg.append("</div>");

		message.setText(msgg.toString(), "utf-8", "html");
		message.setFrom(new InternetAddress("teamtokkit@gmail.com", "Tokkit Team"));

		emailSender.send(message);
		log.info("[Tokkit] 이메일 인증코드 전송 완료: {}", to);
	}
}
