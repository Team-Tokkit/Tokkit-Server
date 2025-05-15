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
import com.example.Tokkit_server.user.dto.response.UserWalletResponseDto;
import com.example.Tokkit_server.user.entity.EmailValidation;
import com.example.Tokkit_server.user.entity.SimplePasswordResetEmailValidation;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.EmailValidationRepository;
import com.example.Tokkit_server.user.repository.PasswordResetEmailValidationRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.wallet.entity.Wallet;
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

        EmailValidation emailValidation = emailValidationRepository.findById(createUserRequestDto.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!emailValidation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        User user = createUserRequestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        // 지갑 생성 후, 직접 set
        Wallet wallet = walletCommandService.createInitialWalletForUser(user.getId());
        user.setWallet(wallet);

        // 알림 설정
        for (NotificationCategory category : NotificationCategory.values()) {
            NotificationCategorySetting setting = NotificationCategorySetting.builder()
                    .user(user)
                    .category(category)
                    .enabled(true)
                    .build();
            notificationSettingRepository.save(setting);
        }

        return UserResponseDto.from(user);
    }

    public UserResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }

    public UserWalletResponseDto getWalletInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return UserWalletResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateUserPassword(String email, UpdateUserPasswordRequestDto userRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (userRequestDto.getPassword() == null || userRequestDto.getNewPassword() == null) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_UPDATE_BAD_REQUEST);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_MATCH);
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