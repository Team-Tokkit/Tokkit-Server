package com.example.Tokkit_server.user.service;

import com.example.Tokkit_server.user.entity.EmailValidation;
import com.example.Tokkit_server.user.entity.SimplePasswordResetEmailValidation;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.EmailValidationRepository;
import com.example.Tokkit_server.user.repository.PasswordResetEmailValidationRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import jakarta.mail.Message;
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


@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private EmailValidationRepository emailRepository;

    @Autowired
    private PasswordResetEmailValidationRepository passwordResetEmailValidationRepository;

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

        String msgg = "";
        msgg += "<div style='margin:20px; font-family:sans-serif;'>";
        msgg += "  <div style='padding:30px; border-radius:12px; background-color:#fff5e0; border:1px solid #ffc66c;'>";
        msgg += "    <h2 style='color:#ff9900;'>Tokkit 이메일 인증</h2>";
        msgg += "    <p style='margin-top:10px; font-size:15px;'>아래 인증번호를 사이트에 입력해 주세요.</p>";
        msgg += "    <div style='margin-top:20px; text-align:center;'>";
        msgg += "      <span style='display:inline-block; padding:15px 25px; background-color:#fff; border:2px dashed #ffcc80; border-radius:10px; font-size:22px; font-weight:bold; color:#333;'>";
        msgg +=        ePw + "</span>";
        msgg += "    </div>";
        msgg += "    <p style='margin-top:20px; font-size:13px; color:#999;'>본 메일은 Tokkit 서비스의 이메일 인증을 위해 발송되었습니다.</p>";
        msgg += "  </div>";
        msgg += "</div>";
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

        String msgg = "";
        msgg += "<div style='margin:20px; font-family:sans-serif;'>";
        msgg += "  <div style='padding:30px; border-radius:12px; background-color:#eaf8ff; border:1px solid #b3e0ff;'>";
        msgg += "    <h2 style='color:#3399ff;'>Tokkit 임시 비밀번호 안내</h2>";
        msgg += "    <p style='margin-top:10px; font-size:15px;'>아래 임시 비밀번호로 로그인한 뒤 반드시 변경해주세요.</p>";
        msgg += "    <div style='margin-top:20px; text-align:center;'>";
        msgg += "      <span style='display:inline-block; padding:15px 25px; background-color:#fff; border:2px dashed #80cfff; border-radius:10px; font-size:22px; font-weight:bold; color:#333;'>";
        msgg +=        newPw + "</span>";
        msgg += "    </div>";
        msgg += "    <p style='margin-top:20px; font-size:13px; color:#999;'>Tokkit 보안을 위해 꼭 비밀번호를 변경해 주세요.</p>";
        msgg += "  </div>";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("Tokkit","토킷")); //보내는 사람

        return message;
    }

    // 간편 비밀번호 변경
    public void sendSimplePasswordVerification(String email) throws Exception {
        String code = createKey();
        saveVerificationCode(email, code);

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("토킷(Tokkit) 간편 비밀번호 변경 인증번호입니다.");

        String msgg = "";
        msgg += "<div style='margin:20px; font-family:sans-serif;'>";
        msgg += "  <div style='padding:30px; border-radius:12px; background-color:#f2f7ff; border:1px solid #a0c4ff;'>";
        msgg += "    <h2 style='color:#3f87ff;'>Tokkit 간편 비밀번호 변경 인증</h2>";
        msgg += "    <p style='margin-top:10px; font-size:15px;'>앱에 아래 인증번호를 입력하여 변경을 완료해 주세요.</p>";
        msgg += "    <div style='margin-top:20px; text-align:center;'>";
        msgg += "      <span style='display:inline-block; padding:15px 25px; background-color:#fff; border:2px dashed #8ecaff; border-radius:10px; font-size:22px; font-weight:bold; color:#333;'>";
        msgg +=        code + "</span>";
        msgg += "    </div>";
        msgg += "    <p style='margin-top:20px; font-size:13px; color:#999;'>토킷 서비스의 보안을 위해 발송된 인증 코드입니다.</p>";
        msgg += "  </div>";
        msgg += "</div>";


        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("Tokkit", "토킷"));

        emailSender.send(message);
    }

    private void saveVerificationCode(String email, String code) {
        passwordResetEmailValidationRepository.deleteById(email); // 기존 삭제
        SimplePasswordResetEmailValidation validation = SimplePasswordResetEmailValidation.builder()
                .email(email)
                .code(code)
                .exp(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5분 유효
                .isVerified(false)
                .build();
        passwordResetEmailValidationRepository.save(validation);
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
        MimeMessage message = createMessage(to);
        try{
            //예외처리
            log.info("[EmailService] 메일 전송 시도중...");

            emailSender.send(message);

            log.info("[EmailService] 메일 전송 성공!");

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