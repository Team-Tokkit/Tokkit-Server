package com.example.Tokkit_server.service;

import java.util.Date;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.EmailValidation;
import com.example.Tokkit_server.domain.user.SimplePasswordResetEmailValidation;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.CreateUserRequestDto;
import com.example.Tokkit_server.dto.request.EmailChangeRequest;
import com.example.Tokkit_server.dto.request.UpdateUserRequestDto;
import com.example.Tokkit_server.dto.request.UserInfoUpdateRequest;
import com.example.Tokkit_server.dto.response.UserResponse;
import com.example.Tokkit_server.repository.EmailValidationRepository;
import com.example.Tokkit_server.repository.PasswordResetEmailValidationRepository;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.service.command.WalletCommandService;
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
    public UserResponse createUser(CreateUserRequestDto createUserRequestDto) {
        if (userRepository.findByEmail(createUserRequestDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        // 이메일 인증 여부 확인
        EmailValidation emailValidation = emailValidationRepository.findById(createUserRequestDto.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!emailValidation.isVerified()) {
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

        return UserResponse.from(user);
    }



    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자가 존재히지 않습니다."));
        return UserResponse.from(user);
    }

    public Long findUserIdByEmail(String email) {
        // 이메일로 사용자 정보를 조회
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 사용자 ID 반환
        return user.getId();
    }


    @Transactional
    public UserResponse updateUser(String email, UpdateUserRequestDto userRequestDto) {
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
        return UserResponse.from(user);
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

        if (!validation.isVerified()) {
            throw new GeneralException(ErrorStatus.VERIFY_ERROR); // 인증 안됨
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateSimplePassword(passwordEncoder.encode(newSimplePassword));
    }

    @Transactional
    public void updateUserInfo(Long userId, UserInfoUpdateRequest requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateNameAndPhoneNumber(requestDto.getName(), requestDto.getPhoneNumber());
    }

    @Transactional
    public void updateEmail(Long userId, EmailChangeRequest requestDto) {
        EmailValidation validation = emailValidationRepository.findById(requestDto.getNewEmail())
            .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!validation.isVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateEmail(requestDto.getNewEmail());
    }
}