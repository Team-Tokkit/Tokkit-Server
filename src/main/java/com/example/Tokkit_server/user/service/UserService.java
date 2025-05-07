package com.example.Tokkit_server.user.service;

import java.util.Date;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.repository.NotificationSettingRepository;
import com.example.Tokkit_server.user.dto.request.CreateUserRequestDto;
import com.example.Tokkit_server.user.dto.request.EmailChangeRequestDto;
import com.example.Tokkit_server.user.dto.request.UpdateUserPasswordRequestDto;
import com.example.Tokkit_server.user.dto.request.UserInfoUpdateRequestDto;
import com.example.Tokkit_server.user.dto.response.UserResponseDto;
import com.example.Tokkit_server.user.entity.EmailValidation;
import com.example.Tokkit_server.user.entity.SimplePasswordResetEmailValidation;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.EmailValidationRepository;
import com.example.Tokkit_server.user.repository.PasswordResetEmailValidationRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.wallet.service.command.WalletCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final EmailValidationRepository emailValidationRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetEmailValidationRepository passwordResetEmailValidationRepository;
    private final WalletCommandService walletCommandService;

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        if (userRepository.findByEmail(createUserRequestDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        // 이메일 인증 여부 확인
        EmailValidation emailValidation = emailValidationRepository.findById(createUserRequestDto.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!emailValidation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        // 유저 생성
        User user = createUserRequestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        // 알림 설정
        for (NotificationCategory category : NotificationCategory.values()) {
            NotificationCategorySetting setting = NotificationCategorySetting.builder()
                    .user(user)
                    .category(category)
                    .enabled(true)
                    .build();
            notificationSettingRepository.save(setting);
        }

        walletCommandService.createInitialWallet(user.getId());

        return UserResponseDto.from(user);
    }



    public UserResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자가 존재히지 않습니다."));
        return UserResponseDto.from(user);
    }

    public Long findUserIdByEmail(String email) {
        // 이메일로 사용자 정보를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 사용자 ID 반환
        return user.getId();
    }


    @Transactional
    public UserResponseDto updateUserPassword(String email, UpdateUserPasswordRequestDto userRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (userRequestDto.getPassword() == null || userRequestDto.getNewPassword() == null) {
            throw new IllegalArgumentException("현재 비밀번호와 새 비밀번호를 모두 입력해야 합니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 인코딩 및 업데이트
        String encodedNewPassword = passwordEncoder.encode(userRequestDto.getNewPassword());
        user.updatePassword(encodedNewPassword);

        userRepository.save(user);
        return UserResponseDto.from(user);
    }

    @Transactional
    public void verifySimplePasswordCode(String email, String code) {
        SimplePasswordResetEmailValidation validation = passwordResetEmailValidationRepository.findById(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (new Date().after(validation.getExp())) {
            throw new GeneralException(ErrorStatus.VERIFY_ERROR);
        }

        if (!validation.getCode().equals(code)) {
            throw new GeneralException(ErrorStatus.VERIFY_ERROR);
        }

        validation.setVerified(true);
        passwordResetEmailValidationRepository.save(validation);
    }

    @Transactional
    public void updateSimplePassword(String email, String newSimplePassword) {
        SimplePasswordResetEmailValidation validation = passwordResetEmailValidationRepository.findById(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!validation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.VERIFY_ERROR); // 인증 안됨
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateSimplePassword(passwordEncoder.encode(newSimplePassword));
    }

    @Transactional
    public void updateUserInfo(Long userId, UserInfoUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateNameAndPhoneNumber(requestDto.getName(), requestDto.getPhoneNumber());
    }

    @Transactional
    public void updateEmail(Long userId, EmailChangeRequestDto requestDto) {
        EmailValidation validation = emailValidationRepository.findById(requestDto.getNewEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!validation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateEmail(requestDto.getNewEmail());
    }
}