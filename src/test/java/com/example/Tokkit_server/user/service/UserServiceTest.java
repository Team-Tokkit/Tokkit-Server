package com.example.Tokkit_server.user.service;

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
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.EmailValidationRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.service.command.WalletCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationSettingRepository notificationSettingRepository;
    @Mock
    private EmailValidationRepository emailValidationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private WalletCommandService walletCommandService;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserRequestDto createUserRequestDto;
    private EmailValidation emailValidation;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user1@mail.com")
                .password("password")
                .name("Test User")
                .phoneNumber("010-1234-5678")
                .simplePassword("000000")
                .build();

        createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.name = "Test User";
        createUserRequestDto.email = "user1@mail.com";
        createUserRequestDto.password = "password";
        createUserRequestDto.phoneNumber = "010-1234-5678";
        createUserRequestDto.simplePassword = "000000";

        emailValidation = EmailValidation.builder()
                .email("user1@mail.com")
                .isVerified(true)
                .build();

        wallet = Wallet.builder()
                .id(1L)
                .user(user)
                .depositBalance(0L)
                .tokenBalance(0L)
                .build();
    }

    @Test
    @DisplayName("createUser - 사용자 이미 존재 예외")
    void createUser_UserAlreadyExistsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.createUser(createUserRequestDto));
        assertEquals(ErrorStatus.USER_ALREADY_EXISTS, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(emailValidationRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("createUser - 이메일 미인증 예외 (EmailValidation 없음)")
    void createUser_EmailNotVerifiedException_NoEmailValidation() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.createUser(createUserRequestDto));
        assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(emailValidationRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("createUser - 이메일 미인증 예외 (isVerified false)")
    void createUser_EmailNotVerifiedException_NotVerified() {
        EmailValidation unverifiedEmailValidation = EmailValidation.builder()
                .email("test@example.com")
                .isVerified(false)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(unverifiedEmailValidation));

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.createUser(createUserRequestDto));
        assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(emailValidationRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("getUser - 성공적으로 사용자 조회")
    void getUser_Success() {
        User mockUser = mock(User.class);
        Wallet mockWallet = mock(Wallet.class);
        lenient().when(mockUser.getEmail()).thenReturn("user1@mail.com");
        lenient().when(mockUser.getName()).thenReturn("Test User");
        lenient().when(mockUser.getPhoneNumber()).thenReturn("010-1234-5678");
        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(mockUser.getSimplePassword()).thenReturn("000000");
        lenient().when(mockUser.getPassword()).thenReturn("password");
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        try {
            UserResponseDto result = userService.getUser("user1@mail.com");
            assertNotNull(result, "UserResponseDto가 null입니다.");
            assertEquals("user1@mail.com", result.getEmail(), "이메일이 일치하지 않습니다.");
            verify(userRepository, times(1)).findByEmail(anyString());
        } catch (Exception e) {
            fail("getUser_Success 테스트에서 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("getUser - 사용자 조회 실패 (User Not Found)")
    void getUser_UserNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.getUser("nonexistent@example.com"));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("updateUserPassword - 성공적인 비밀번호 업데이트")
    void updateUserPassword_Success() {
        // Given
        User mockUser = mock(User.class);
        Wallet mockWallet = mock(Wallet.class);

        // Mock User getters
        lenient().when(mockUser.getEmail()).thenReturn("user1@mail.com");
        lenient().when(mockUser.getPassword()).thenReturn("encodedPassword");
        lenient().when(mockUser.getName()).thenReturn("Test User");
        lenient().when(mockUser.getPhoneNumber()).thenReturn("010-1234-5678");
        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(mockUser.getSimplePassword()).thenReturn("000000");
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);
        lenient().when(mockUser.getRoles()).thenReturn("USER");
        lenient().when(mockUser.getIsDormant()).thenReturn(false);

        UpdateUserPasswordRequestDto requestDto = new UpdateUserPasswordRequestDto("password", "newPassword");

        // Mock repository and service behavior
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        UserResponseDto result = userService.updateUserPassword("user1@mail.com", requestDto);

        // Then
        assertNotNull(result, "UserResponseDto가 null입니다.");
        assertEquals("user1@mail.com", result.getEmail(), "이메일이 일치하지 않습니다. 실제 값: " + result.getEmail());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("updateUserPassword - 사용자 조회 실패 (User Not Found)")
    void updateUserPassword_UserNotFound() {
        UpdateUserPasswordRequestDto requestDto = new UpdateUserPasswordRequestDto("oldPassword", "newPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateUserPassword("nonexistent@example.com", requestDto));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("updateUserPassword - 비밀번호 업데이트 요청 Bad Request (Null Passwords)")
    void updateUserPassword_BadRequest_NullPasswords() {
        UpdateUserPasswordRequestDto requestDto = new UpdateUserPasswordRequestDto(null, null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateUserPassword("test@example.com", requestDto));
        assertEquals(ErrorStatus.USER_PASSWORD_UPDATE_BAD_REQUEST, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("updateUserPassword - 현재 비밀번호 불일치")
    void updateUserPassword_PasswordNotMatch() {
        // Given
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn("encodedPassword");
        UpdateUserPasswordRequestDto requestDto = new UpdateUserPasswordRequestDto("wrongPassword", "newPassword");

        // Mock repository and service behavior
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateUserPassword("user1@mail.com", requestDto));
        assertEquals(ErrorStatus.USER_PASSWORD_NOT_MATCH, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateSimplePassword - 성공적인 간편 비밀번호 업데이트")
    void updateSimplePassword_Success() {
        String newSimplePassword = "1234";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newSimplePassword)).thenReturn("encodedNewSimplePassword");

        userService.updateSimplePassword("test@example.com", newSimplePassword);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(newSimplePassword);
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewSimplePassword", user.getSimplePassword());
    }

    @Test
    @DisplayName("updateSimplePassword - 사용자 조회 실패 (User Not Found)")
    void updateSimplePassword_UserNotFound() {
        String newSimplePassword = "1234";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateSimplePassword("nonexistent@example.com", newSimplePassword));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("verifySimplePassword - 성공적인 간편 비밀번호 검증")
    void verifySimplePassword_Success() {
        // Given
        User mockUser = mock(User.class);
        String simplePassword = "1234";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(mockUser.matchSimplePassword(simplePassword, passwordEncoder)).thenReturn(true);

        // When
        userService.verifySimplePassword("test@example.com", simplePassword);

        // Then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(mockUser, times(1)).matchSimplePassword(simplePassword, passwordEncoder);
    }

    @Test
    @DisplayName("verifySimplePassword - 사용자 조회 실패 (User Not Found)")
    void verifySimplePassword_UserNotFound() {
        String simplePassword = "1234";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.verifySimplePassword("nonexistent@example.com", simplePassword));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("verifySimplePassword - 간편 비밀번호 불일치")
    void verifySimplePassword_InvalidSimplePassword() {
        // Given
        User mockUser = mock(User.class);
        String simplePassword = "wrongPassword";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(mockUser.matchSimplePassword(simplePassword, passwordEncoder)).thenReturn(false);

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.verifySimplePassword("test@example.com", simplePassword));
        assertEquals(ErrorStatus.INVALID_SIMPLE_PASSWORD, exception.getErrorStatus());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(mockUser, times(1)).matchSimplePassword(simplePassword, passwordEncoder);
    }

    @Test
    @DisplayName("updateUserInfo - 성공적인 사용자 정보 업데이트")
    void updateUserInfo_Success() {
        UserInfoUpdateRequestDto requestDto = UserInfoUpdateRequestDto.builder()
                .name("Updated Name")
                .phoneNumber("010-9876-5432")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.updateUserInfo(1L, requestDto);

        verify(userRepository, times(1)).findById(anyLong());
        assertEquals("Updated Name", user.getName());
        assertEquals("010-9876-5432", user.getPhoneNumber());
    }

    @Test
    @DisplayName("updateUserInfo - 사용자 조회 실패 (User Not Found)")
    void updateUserInfo_UserNotFound() {
        UserInfoUpdateRequestDto requestDto = UserInfoUpdateRequestDto.builder()
                .name("Updated Name")
                .phoneNumber("010-9876-5432")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateUserInfo(1L, requestDto));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("updateUserInfo - 이름과 전화번호 필드가 null일 때 사용자 정보 업데이트")
    void updateUserInfo_NullFields() {
        UserInfoUpdateRequestDto requestDto = UserInfoUpdateRequestDto.builder()
                .name(null)
                .phoneNumber(null)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.updateUserInfo(1L, requestDto);

        verify(userRepository, times(1)).findById(anyLong());
        assertNull(user.getName());
        assertNull(user.getPhoneNumber());
    }

    @Test
    @DisplayName("updateUserInfo - 이름과 전화번호 필드가 빈 문자열일 때 사용자 정보 업데이트")
    void updateUserInfo_EmptyFields() {
        UserInfoUpdateRequestDto requestDto = UserInfoUpdateRequestDto.builder()
                .name("")
                .phoneNumber("")
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.updateUserInfo(1L, requestDto);

        verify(userRepository, times(1)).findById(anyLong());
        assertEquals("", user.getName());
        assertEquals("", user.getPhoneNumber());
    }

    @Test
    @DisplayName("updateEmail - 성공적인 이메일 업데이트")
    void updateEmail_Success() {
        EmailChangeRequestDto requestDto = EmailChangeRequestDto.builder()
                .newEmail("new@example.com")
                .build();
        EmailValidation newEmailValidation = EmailValidation.builder()
                .email("new@example.com")
                .isVerified(true)
                .build();
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(newEmailValidation));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.updateEmail(1L, requestDto);

        verify(emailValidationRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).findById(anyLong());
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("updateEmail - 새로운 이메일 미인증 (EmailValidation 없음)")
    void updateEmail_NewEmailNotVerified_NoEmailValidation() {
        EmailChangeRequestDto requestDto = EmailChangeRequestDto.builder()
                .newEmail("new@example.com")
                .build();
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateEmail(1L, requestDto));
        assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        verify(emailValidationRepository, times(1)).findById(anyString());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateEmail - 새로운 이메일 미인증 (isVerified false)")
    void updateEmail_NewEmailNotVerified_NotVerified() {
        EmailChangeRequestDto requestDto = EmailChangeRequestDto.builder()
                .newEmail("new@example.com")
                .build();
        EmailValidation newEmailValidation = EmailValidation.builder()
                .email("new@example.com")
                .isVerified(false)
                .build();
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(newEmailValidation));

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateEmail(1L, requestDto));
        assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        verify(emailValidationRepository, times(1)).findById(anyString());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateEmail - 사용자 조회 실패 (User Not Found)")
    void updateEmail_UserNotFound() {
        EmailChangeRequestDto requestDto = EmailChangeRequestDto.builder()
                .newEmail("new@example.com")
                .build();
        EmailValidation newEmailValidation = EmailValidation.builder()
                .email("new@example.com")
                .isVerified(true)
                .build();
        when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(newEmailValidation));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class,
                () -> userService.updateEmail(1L, requestDto));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(emailValidationRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }
}
