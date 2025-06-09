package com.example.Tokkit_server.notification.service;

import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.notification.entity.Notification;
import com.example.Tokkit_server.notification.entity.NotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.enums.NotificationTemplate;
import com.example.Tokkit_server.notification.repository.NotificationRepository;
import com.example.Tokkit_server.notification.repository.NotificationSettingRepository;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.user.utils.SseEmitters;
import com.example.Tokkit_server.user.dto.response.NotificationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationSettingRepository notificationSettingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SseEmitters sseEmitters;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private SseNotificationService sseNotificationService;

    @InjectMocks
    @Spy
    private NotificationServiceImpl notificationService;

    private User testUser;
    private NotificationCategorySetting enabledEmailSetting;
    private NotificationCategorySetting disabledEmailSetting;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        enabledEmailSetting = NotificationCategorySetting.builder()
                .user(testUser)
                .category(NotificationCategory.SYSTEM)
                .enabled(true)
                .build();

        disabledEmailSetting = NotificationCategorySetting.builder()
                .user(testUser)
                .category(NotificationCategory.SYSTEM)
                .enabled(false)
                .build();

        // ReflectionTestUtils를 사용하여 private final 필드 주입
    }

    @Test
    @DisplayName("sendNotification - SSE와 이메일 모두 전송되는 경우")
    void sendNotification_sendSseAndEmail() {
        // Given
        NotificationTemplate template = NotificationTemplate.SYSTEM_MAINTENANCE; // isSendSse=true, isSendEmail=true
        when(notificationSettingRepository.findByUserAndCategory(testUser, template.getCategory()))
                .thenReturn(enabledEmailSetting);
        when(sseNotificationService.sendSse(anyLong(), anyString(), anyString())).thenReturn(true);
        when(emailNotificationService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.sendNotification(testUser, template, "업데이트 내용");

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class)); // 초기 저장 및 상태 업데이트 저장
        verify(sseNotificationService, times(1)).sendSse(eq(testUser.getId()), anyString(), anyString());
        verify(emailNotificationService, times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("sendNotification - SSE만 전송되고 이메일 수신 거부인 경우")
    void sendNotification_sendSseOnly_emailDisabled() {
        // Given
        NotificationTemplate template = NotificationTemplate.SYSTEM_MAINTENANCE; // isSendSse=true, isSendEmail=true
        when(notificationSettingRepository.findByUserAndCategory(testUser, template.getCategory()))
                .thenReturn(disabledEmailSetting); // 이메일 수신 거부
        when(sseNotificationService.sendSse(anyLong(), anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.sendNotification(testUser, template, "업데이트 내용");

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(sseNotificationService, times(1)).sendSse(eq(testUser.getId()), anyString(), anyString());
        verify(emailNotificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("sendNotification - SSE가 전송되지 않고 이메일만 전송되는 경우")
    void sendNotification_sendEmailOnly_sseNotConfigured() {
        // Given
        NotificationTemplate template = NotificationTemplate.TOKEN_CONVERTED; // isSendSse=false, isSendEmail=true
        when(notificationSettingRepository.findByUserAndCategory(testUser, template.getCategory()))
                .thenReturn(enabledEmailSetting);
        when(emailNotificationService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.sendNotification(testUser, template, 1000);

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(sseNotificationService, never()).sendSse(anyLong(), anyString(), anyString());
        verify(emailNotificationService, times(1)).sendEmail(eq(testUser.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("subscribe - SSE Emitter 구독 성공")
    void subscribe_success() throws IOException {
        // Given
        when(sseEmitters.get(anyLong())).thenReturn(null); // 기존 emitter 없음
        doNothing().when(sseEmitters).add(anyLong(), any(SseEmitter.class));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        // sendUnsentNotificationsAsync 호출될 때 sendUnsentNotifications 내부 로직이 실제로 실행되지
        // 않도록 Mocking
        doNothing().when(notificationService).sendUnsentNotificationsAsync(any(User.class));

        // When
        SseEmitter emitter = notificationService.subscribe(testUser.getId());

        // Then
        assertNotNull(emitter);
        verify(sseEmitters, times(1)).add(eq(testUser.getId()), any(SseEmitter.class));
        verify(userRepository, times(1)).findById(eq(testUser.getId()));
        // verify(notificationService, times(1)).sendUnsentNotificationsAsync(testUser);
        // // sendUnsentNotificationsAsync가 실행되는지 확인
    }

    @Test
    @DisplayName("deleteNotification - 알림 삭제 성공")
    void deleteNotification_success() {
        // Given
        Long notificationId = 100L;
        Notification notification = Notification.builder()
                .id(notificationId)
                .user(testUser)
                .build();
        when(notificationRepository.findByIdAndUser(notificationId, testUser)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.deleteNotification(notificationId, testUser);

        // Then
        assertTrue(notification.isDeleted());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("deleteNotification - 알림을 찾을 수 없는 경우 GeneralException 발생")
    void deleteNotification_notificationNotFound() {
        // Given
        Long notificationId = 100L;
        when(notificationRepository.findByIdAndUser(notificationId, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GeneralException.class, () -> notificationService.deleteNotification(notificationId, testUser));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("deleteNotification - 다른 유저의 알림 삭제 시도 시 GeneralException 발생")
    void deleteNotification_forbidden() {
        // Given
        Long notificationId = 100L;
        User anotherUser = User.builder().id(2L).build();
        Notification notification = Notification.builder()
                .id(notificationId)
                .user(anotherUser) // 다른 유저의 알림
                .build();
        when(notificationRepository.findByIdAndUser(notificationId, testUser)).thenReturn(Optional.of(notification));

        // When & Then
        assertThrows(GeneralException.class, () -> notificationService.deleteNotification(notificationId, testUser));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendUnsentNotifications - 미전송 알림 모두 전송 성공")
    void sendUnsentNotifications_allSentSuccess() {
        // Given
        Notification unsentSseNotification = Notification.builder()
                .id(1L)
                .user(testUser)
                .category(NotificationCategory.SYSTEM)
                .title("SSE 미전송")
                .content("SSE 미전송 내용")
                .sentSse(false)
                .sentMail(true)
                .build();

        Notification unsentEmailNotification = Notification.builder()
                .id(2L)
                .user(testUser)
                .category(NotificationCategory.SYSTEM)
                .title("이메일 미전송")
                .content("이메일 미전송 내용")
                .sentSse(true)
                .sentMail(false)
                .build();

        Notification bothUnsentNotification = Notification.builder()
                .id(3L)
                .user(testUser)
                .category(NotificationCategory.SYSTEM)
                .title("둘 다 미전송")
                .content("둘 다 미전송 내용")
                .sentSse(false)
                .sentMail(false)
                .build();

        when(notificationRepository.findByUserAndDeletedFalse(testUser))
                .thenReturn(List.of(unsentSseNotification, unsentEmailNotification, bothUnsentNotification));
        when(sseNotificationService.sendSse(anyLong(), anyString(), anyString())).thenReturn(true);
        when(notificationSettingRepository.findByUserAndCategory(any(User.class), any(NotificationCategory.class)))
                .thenReturn(enabledEmailSetting);
        when(emailNotificationService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        when(notificationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificationService.sendUnsentNotifications(testUser);

        // Then
        assertTrue(unsentSseNotification.isSentSse());
        assertTrue(unsentEmailNotification.isSentMail());
        assertTrue(bothUnsentNotification.isSentSse());
        assertTrue(bothUnsentNotification.isSentMail());

        verify(sseNotificationService, times(2)).sendSse(anyLong(), anyString(), anyString()); // unsentSseNotification,
                                                                                               // bothUnsentNotification
        verify(emailNotificationService, times(2)).sendEmail(anyString(), anyString(), anyString()); // unsentEmailNotification,
                                                                                                     // bothUnsentNotification
        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("getAllNotifications - 모든 알림 조회 성공")
    void getAllNotifications_success() {
        // Given
        Notification notification1 = Notification.builder().id(1L).user(testUser).category(NotificationCategory.SYSTEM)
                .build();
        Notification notification2 = Notification.builder().id(2L).user(testUser).category(NotificationCategory.PAYMENT)
                .build();

        NotificationCategorySetting serviceSetting = NotificationCategorySetting.builder().user(testUser)
                .category(NotificationCategory.SYSTEM).enabled(true).build();
        NotificationCategorySetting adSetting = NotificationCategorySetting.builder().user(testUser)
                .category(NotificationCategory.PAYMENT).enabled(true).build();

        when(notificationSettingRepository.findByUserAndEnabledTrue(testUser))
                .thenReturn(List.of(serviceSetting, adSetting));
        when(notificationRepository.findByUserAndCategoriesAndDeletedFalse(
                eq(testUser), anyList()))
                .thenReturn(List.of(notification1, notification2));

        // When
        List<NotificationResponseDto> result = notificationService.getAllNotifications(testUser);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationSettingRepository, times(1)).findByUserAndEnabledTrue(testUser);
        verify(notificationRepository, times(1)).findByUserAndCategoriesAndDeletedFalse(eq(testUser), anyList());
    }
}