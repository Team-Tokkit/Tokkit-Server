package com.example.Tokkit_server.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.example.Tokkit_server.domain.user.EmailValidation;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.repository.EmailValidationRepository;
import com.example.Tokkit_server.repository.UserRepository;

@Service
@Slf4j
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private EmailValidationRepository emailRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 인증 번호 전송
	private MimeMessage createMessage(String to)throws Exception{
		String ePw = createKey();

		isnertDB(to, ePw);
		MimeMessage  message = emailSender.createMimeMessage();

		message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
		message.setSubject("토킷(Tokkit) 이메일 인증입니다.");//제목

		String msgg="";
		msgg+= "<div style='margin:20px;'>";
		msgg+= "<h1> 토킷(Tokkit) 인증번호입니다. </h1>";
		msgg+= "<br>";
		msgg+= "<p>아래 인증번호를 입력해주세요.<p>";
		msgg+= "<br>";
		msgg+= "<div align='center' style='border:1px dashed black; font-family:verdana';>";
		msgg+= "<h3 style='color:skyblue;'>임시 인증번호</h3>";
		msgg+= "<div style='font-size:130%'>";
		msgg+= "CODE : <strong>";
		msgg+= ePw+"</strong><div><br/> ";
		msgg+= "</div>";
		message.setText(msgg, "utf-8", "html");//내용
		message.setFrom(new InternetAddress("Tokkit","토킷"));//보내는 사람

		return message;
	}

	// 임시 비밀번호 발급
	private MimeMessage createPasswordMessage(String to)throws Exception{
		String newPw = createPassword();
		MimeMessage  message = emailSender.createMimeMessage();


		insertPw(to, newPw);
		message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
		message.setSubject("토킷(Tokkit) 임시 비밀번호입니다.");//제목

		String msgg="";
		msgg+= "<div style='margin:20px;'>";
		msgg+= "<h1> 토킷(Tokkit) 임시 인증비밀번호입니다. </h1>";
		msgg+= "<br>";
		msgg+= "<p>로그인 후 꼭 비밀번호를 변경하세요.<p>";
		msgg+= "<br>";
		msgg+= "<div align='center' style='border:1px dashed black; font-family:verdana';>";
		msgg+= "<h3 style='color:skyblue;'>임시 비밀번호</h3>";
		msgg+= "<div style='font-size:130%'>";
		msgg+= "CODE : <strong>";
		msgg+= newPw+"</strong><div><br/> ";
		msgg+= "</div>";
		message.setText(msgg, "utf-8", "html");//내용
		message.setFrom(new InternetAddress("Tookit","토킷"));//보내는 사람

		return message;
	}


	public static String createKey() {
		StringBuffer key = new StringBuffer();
		Random rnd = new Random();

		for (int i = 0; i < 4; i++) { // 비밀번호 4자리
			int index = rnd.nextInt(3); // 0~2 까지 랜덤

			switch (index) {
				case 0 -> key.append((char) ((int) (rnd.nextInt(26)) + 97));

				//  a~z  (ex. 1+97=98 => (char)98 = 'b')
				case 1 -> key.append((char) ((int) (rnd.nextInt(26)) + 65));

				//  A~Z
				case 2 -> key.append((rnd.nextInt(10)));

				// 0~9
			}
		}
		return key.toString();
	}

	public static String createPassword() {
		StringBuffer key = new StringBuffer();
		Random rnd = new Random();


		for (int i = 0; i < 6; i++) { // 비밀번호 6자리
			int index = rnd.nextInt(3); // 0~2 까지 랜덤

			switch (index) {
				case 0 -> key.append((char) ((int) (rnd.nextInt(26)) + 97));

				//  a~z  (ex. 1+97=98 => (char)98 = 'b')
				case 1 -> key.append((char) ((int) (rnd.nextInt(26)) + 65));

				//  A~Z
				case 2 -> key.append((rnd.nextInt(10)));

				// 0~9
			}
		}
		return key.toString();
	}

	public void sendMessage(String to)throws Exception {
		// TODO Auto-generated method stub
		MimeMessage message = createMessage(to);
		try{//예외처리
			emailSender.send(message);
			//TODO : 비밀번호 바꾸기 (DB)
		}catch(MailException es){
			es.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void sendMessageForPassword(String to)throws Exception {
		// TODO Auto-generated method stub
		MimeMessage message = createPasswordMessage(to);
		try{//예외처리
			emailSender.send(message);
		}catch(MailException es){
			es.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	private void isnertDB(String email, String ePw){
		List<EmailValidation> emailValidationList = emailRepository.findAllByEmail(email);
		if (!emailValidationList.isEmpty()) {
			emailRepository.deleteAll(emailValidationList);
		}

		Date now = new Date();
		Long EMAIL_EXP = 1000L * 60 * 5; // 5분
		Date exp = new Date(now.getTime() + EMAIL_EXP);

		EmailValidation emailValidation = EmailValidation.builder()
			.email(email)
			.exp(exp)
			.ePw(ePw)
			.isVerified(false)  // 인증 처음엔 false로
			.build();
		emailRepository.save(emailValidation);
	}

	public boolean ValidationCheck(String email, String code){
		EmailValidation emailValidation = emailRepository.findById(email)
			.orElseThrow(() -> new RuntimeException("Invalid value: email not found"));
		Date now = new Date();

		if (now.after(emailValidation.getExp())) {
			throw new RuntimeException("Expired password code");
		}

		if (emailValidation.getEPw().equals(code)) {
			// 인증 성공하면 isVerified = true 업데이트
			EmailValidation verified = EmailValidation.builder()
				.email(emailValidation.getEmail())
				.exp(emailValidation.getExp())
				.ePw(emailValidation.getEPw())
				.isVerified(true)
				.build();
			emailRepository.save(verified);
			return true;
		} else {
			throw new RuntimeException("Invalid password code");
		}
	}


	private void insertPw(String email, String pw) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		user.updatePassword(passwordEncoder.encode(pw));
		userRepository.save(user);
	}
}