package com.example.Tokkit_server.notification.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            // HTML 형식으로 메일 본문 구성
            String html = """
                <div style="max-width:600px; margin:20px auto; font-family:'Segoe UI', sans-serif; background-color:#fffbe6; padding:30px; border-radius:12px; border:1px solid #ffd666;">
                    <h2 style="color:#faad14;">Tokkit 알림</h2>
                    <p style="font-size:15px; color:#333;">%s</p>
                    <p style="margin-top:30px; font-size:13px; color:#999;">본 메일은 Tokkit 서비스에 의해 발송되었습니다.</p>
                </div>
            """.formatted(text);

            message.setText(html, "utf-8", "html");
            message.setFrom(new InternetAddress("Tokkit", "토킷"));

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return false;
        }
    }
}
