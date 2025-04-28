package com.example.Tokkit_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailSender emailSender;
	private final Map<String, String> emailAuthCodeStorage = new ConcurrentHashMap<>();
	private final Random random = new Random();

	public void sendAuthCode(String email) {
		String code = String.format("%06d", random.nextInt(1000000)); // 6자리 랜덤 인증 코드 생성
		try {
			emailSender.sendAuthCodeEmail(email, code);
		} catch (Exception e) {
			throw new RuntimeException("이메일 전송에 실패했습니다.", e);
		}
		emailAuthCodeStorage.put(email, code); // 코드 저장
	}

	public boolean verifyAuthCode(String email, String inputCode) {
		String savedCode = emailAuthCodeStorage.get(email);
		return savedCode != null && savedCode.equals(inputCode);
	}
}
