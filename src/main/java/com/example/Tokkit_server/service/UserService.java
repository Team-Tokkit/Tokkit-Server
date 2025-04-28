package com.example.Tokkit_server.service;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.SignUpRequest;
import com.example.Tokkit_server.dto.response.SignUpResonse;
import com.example.Tokkit_server.jwt.JwtProvider;
import com.example.Tokkit_server.repository.NotificationSettingRepository;
import com.example.Tokkit_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignUpResonse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .simplePassword(passwordEncoder.encode(request.getSimplePassword()))
            .isDormant(false)
            .build();
        userRepository.save(user);

        // Wallet 생성 스킵 또는 추후
        // Wallet wallet = walletCommandService.createWalletForUser(user);

        // ✅ 알림 설정 4개 생성 (SYSTEM, PAYMENT, VOUCHER, TOKEN)
        for (NotificationCategory category : NotificationCategory.values()) {
            NotificationCategorySetting setting = NotificationCategorySetting.builder()
                .user(user)
                .category(category)
                .enabled(true)
                .build();
            notificationSettingRepository.save(setting);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId());

        return SignUpResonse.of(
            accessToken,
            null // 아직 Wallet 없음
        );
    }

}