package com.example.Tokkit_server.service;

import java.util.Optional;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.user.EmailValidation;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.CreateUserRequestDto;
import com.example.Tokkit_server.dto.request.UpdateUserRequestDto;
import com.example.Tokkit_server.dto.response.UserResponse;
import com.example.Tokkit_server.repository.EmailValidationRepository;
import com.example.Tokkit_server.utils.JwtUtil;
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
    private final EmailValidationRepository emailValidationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse createUser(CreateUserRequestDto createUserRequestDto) {
        if (userRepository.findByEmail(createUserRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 유저입니다.");
        }

        // 이메일 인증 여부 확인
        EmailValidation emailValidation = emailValidationRepository.findById(createUserRequestDto.getEmail())
            .orElseThrow(() -> new RuntimeException("이메일 인증을 진행하세요."));

        if (!emailValidation.isVerified()) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 유저 엔티티 생성 및 저장
        User user = createUserRequestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        // 알림 설정 생성
        for (NotificationCategory category : NotificationCategory.values()) {
            NotificationCategorySetting setting = NotificationCategorySetting.builder()
                .user(user)
                .category(category)
                .enabled(true)
                .build();
            notificationSettingRepository.save(setting);
        }

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
}