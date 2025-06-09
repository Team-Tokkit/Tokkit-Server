package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.dto.request.CreateMerchantRequestDto;
import com.example.Tokkit_server.merchant.dto.request.MerchantEmailChangeRequestDto;
import com.example.Tokkit_server.merchant.dto.request.UpdateMerchantPasswordRequestDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantRegisterResponseDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantResponseDto;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.entity.MerchantEmailValidation;
import com.example.Tokkit_server.merchant.repository.MerchantEmailValidationRepository;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.notification.entity.MerchantNotificationCategorySetting;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.notification.repository.MerchantNotificationSettingRepository;
import com.example.Tokkit_server.ocr.service.KakaoAddressSearchService;
import com.example.Tokkit_server.ocr.utils.KakaoGeoResult;
import com.example.Tokkit_server.region.entity.Region;
import com.example.Tokkit_server.region.repository.RegionRepository;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.enums.StoreCategory;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.service.command.WalletCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Tokkit_server.wallet.enums.WalletType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

        @Mock
        private MerchantRepository merchantRepository;
        @Mock
        private MerchantEmailValidationRepository emailValidationRepository;
        @Mock
        private RegionRepository regionRepository;
        @Mock
        private StoreRepository storeRepository;
        @Mock
        private WalletCommandService walletCommandService;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private KakaoAddressSearchService kakaoAddressSearchService;
        @Mock
        private MerchantNotificationSettingRepository notificationSettingRepository;

        @InjectMocks
        private MerchantService merchantService;

        private CreateMerchantRequestDto createRequestDto;
        private MerchantEmailValidation verifiedEmail;

        @Mock
        private Merchant merchant;
        @Mock
        private Store store;
        @Mock
        private Wallet wallet;
        @Mock
        private Region regionMock;

        private KakaoGeoResult kakaoGeoResult;

        @BeforeEach
        void setUp() {
                createRequestDto = new CreateMerchantRequestDto(
                                "Test Name",
                                "test@example.com",
                                "010-1234-5678",
                                "password123",
                                "1234",
                                "123-45-67890",
                                "Test Store",
                                "서울시 강남구 테헤란로",
                                "서울시",
                                "강남구",
                                StoreCategory.FOOD);

                verifiedEmail = MerchantEmailValidation.builder()
                                .email("test@example.com")
                                .isVerified(true)
                                .build();

                kakaoGeoResult = new KakaoGeoResult(127.5678, 37.1234, "01234");
        }

        // 테스트 클래스 상단에 추가 (다른 @Mock 어노테이션들과 함께)
        @Mock
        private Wallet mockWallet;

        @Test
        @DisplayName("createMerchant - 회원가입 성공")
        void createMerchant_success() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
                when(merchantRepository.existsByBusinessNumber(anyString())).thenReturn(false);
                when(regionRepository.findBySidoNameAndSigunguName(anyString(), anyString()))
                                .thenReturn(Optional.of(regionMock));
                when(kakaoAddressSearchService.search(anyString())).thenReturn(Optional.of(kakaoGeoResult));
                when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

                // Fix: Mock the return value instead of using doNothing()
                // Option 1: If the method returns a Wallet object
                when(walletCommandService.createInitialWalletForMerchant(any())).thenReturn(mock(Wallet.class));

                // Option 2: If the method returns void but throws exception, use lenient
                // lenient().when(walletCommandService.createInitialWalletForMerchant(any())).thenReturn(null);

                // When
                MerchantRegisterResponseDto response = merchantService.createMerchant(createRequestDto);

                // Then
                assertNotNull(response);
                verify(merchantRepository, times(1)).save(any(Merchant.class));
                verify(walletCommandService, times(1)).createInitialWalletForMerchant(any());
        }

        @Test
        @DisplayName("createMerchant - 이메일 인증이 안 된 경우 GeneralException 발생")
        void createMerchant_emailNotVerified() {
                // Given
                MerchantEmailValidation unverifiedEmail = MerchantEmailValidation.builder()
                                .email("test@example.com")
                                .isVerified(false)
                                .build();
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(unverifiedEmail));

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - 이메일 인증 정보가 없는 경우 GeneralException 발생")
        void createMerchant_emailValidationNotFound() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - 이메일이 이미 존재하는 경우 GeneralException 발생")
        void createMerchant_emailAlreadyExists() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(true);

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.MERCHANT_ALREADY_EXISTS, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - 사업자 등록 번호가 이미 존재하는 경우 GeneralException 발생")
        void createMerchant_businessNumberAlreadyExists() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
                when(merchantRepository.existsByBusinessNumber(anyString())).thenReturn(true);

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.MERCHANT_ALREADY_EXISTS, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - Region을 찾을 수 없는 경우 GeneralException 발생")
        void createMerchant_regionNotFound() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
                when(merchantRepository.existsByBusinessNumber(anyString())).thenReturn(false);
                when(regionRepository.findBySidoNameAndSigunguName(anyString(), anyString()))
                                .thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.REGION_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - 주소를 찾을 수 없는 경우 GeneralException 발생")
        void createMerchant_addressNotFound() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
                when(merchantRepository.existsByBusinessNumber(anyString())).thenReturn(false);
                when(regionRepository.findBySidoNameAndSigunguName(anyString(), anyString()))
                                .thenReturn(Optional.of(regionMock));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);
                when(kakaoAddressSearchService.search(anyString())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.ADDRESS_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("createMerchant - 카카오 주소 검색 실패 시 GeneralException 발생")
        void createMerchant_kakaoAddressSearchFailed() {
                // Given
                when(emailValidationRepository.findTopByEmailOrderByExpDesc(anyString()))
                                .thenReturn(Optional.of(verifiedEmail));
                when(merchantRepository.existsByEmail(anyString())).thenReturn(false);
                when(merchantRepository.existsByBusinessNumber(anyString())).thenReturn(false);
                when(regionRepository.findBySidoNameAndSigunguName(anyString(), anyString()))
                                .thenReturn(Optional.of(regionMock));
                when(kakaoAddressSearchService.search(anyString())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.createMerchant(createRequestDto));
                assertEquals(ErrorStatus.ADDRESS_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("getInfo - 가맹점을 찾을 수 없는 경우 GeneralException 발생")
        void getInfo_merchantNotFound() {
                // Given
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class, () -> merchantService.getInfo(1L));
                assertEquals(ErrorStatus.MERCHANT_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("updateMerchantPassword - 가맹점을 찾을 수 없는 경우 GeneralException 발생")
        void updateMerchantPassword_merchantNotFound() {
                // Given
                UpdateMerchantPasswordRequestDto requestDto = new UpdateMerchantPasswordRequestDto("oldPassword",
                                "newPassword");
                when(merchantRepository.findByBusinessNumber(anyString())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.updateMerchantPassword("123-45-67890", requestDto));
                assertEquals(ErrorStatus.MERCHANT_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("updateMerchantPassword - 비밀번호 입력이 없는 경우 GeneralException 발생")
        void updateMerchantPassword_badRequest() {
                // Given
                UpdateMerchantPasswordRequestDto requestDto = new UpdateMerchantPasswordRequestDto(null, "newPassword");
                when(merchantRepository.findByBusinessNumber(anyString())).thenReturn(Optional.of(merchant));

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.updateMerchantPassword("123-45-67890", requestDto));
                assertEquals(ErrorStatus.MERCHANT_PASSWORD_UPDATE_BAD_REQUEST, exception.getErrorStatus());
        }

        @Test
        @DisplayName("verifySimplePassword - 간편 비밀번호 검증 성공")
        void verifySimplePassword_success() {
                // Given
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));
                // Directly stub the method call on the mock merchant
                when(merchant.matchSimplePassword(any(PasswordEncoder.class), anyString())).thenReturn(true);

                // When
                merchantService.verifySimplePassword(1L, "1234");

                // Then
                verify(merchant, times(1)).matchSimplePassword(any(PasswordEncoder.class), eq("1234"));
        }

        @Test
        @DisplayName("verifySimplePassword - 가맹점을 찾을 수 없는 경우 GeneralException 발생")
        void verifySimplePassword_merchantNotFound() {
                // Given
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.verifySimplePassword(1L, "1234"));
                assertEquals(ErrorStatus.MERCHANT_NOT_FOUND, exception.getErrorStatus());
        }

        @Test
        @DisplayName("verifySimplePassword - 간편 비밀번호가 일치하지 않는 경우 GeneralException 발생")
        void verifySimplePassword_invalidSimplePassword() {
                // Given
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));
                when(merchant.matchSimplePassword(any(PasswordEncoder.class), anyString())).thenReturn(false);

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.verifySimplePassword(1L, "1234"));
                assertEquals(ErrorStatus.INVALID_SIMPLE_PASSWORD, exception.getErrorStatus());
        }

        @Test
        @DisplayName("updateSimplePassword - 간편 비밀번호 변경 성공")
        void updateSimplePassword_success() {
                // Given
                String newSimplePassword = "new1234";
                when(merchantRepository.findByBusinessNumber(anyString())).thenReturn(Optional.of(merchant));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedNewSimplePassword");
                when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

                // When
                merchantService.updateSimplePassword("123-45-67890", newSimplePassword);

                // Then
                verify(merchant, times(1)).updateSimplePassword(eq("encodedNewSimplePassword"));
                verify(merchantRepository, times(1)).save(merchant);
        }

        @Test
        @DisplayName("updateEmail - 이메일 변경 성공")
        void updateEmail_success() {
                // Given
                MerchantEmailChangeRequestDto requestDto = new MerchantEmailChangeRequestDto("newtest@example.com");
                MerchantEmailValidation newEmailValidation = MerchantEmailValidation.builder()
                                .email("newtest@example.com")
                                .isVerified(true)
                                .build();
                when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(newEmailValidation));
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));

                // When
                merchantService.updateEmail(1L, requestDto);

                // Then
                verify(merchant, times(1)).updateEmail(eq("newtest@example.com"));
        }

        @Test
        @DisplayName("updateEmail - 새로운 이메일이 인증되지 않은 경우 GeneralException 발생")
        void updateEmail_newEmailNotVerified() {
                // Given
                MerchantEmailChangeRequestDto requestDto = new MerchantEmailChangeRequestDto("newtest@example.com");
                MerchantEmailValidation unverifiedNewEmail = MerchantEmailValidation.builder()
                                .email("newtest@example.com")
                                .isVerified(false)
                                .build();
                when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(unverifiedNewEmail));

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.updateEmail(1L, requestDto));
                assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        }

        @Test
        @DisplayName("updateEmail - 이메일 인증 정보를 찾을 수 없는 경우 GeneralException 발생")
        void updateEmail_emailValidationNotFound() {
                // Given
                MerchantEmailChangeRequestDto requestDto = new MerchantEmailChangeRequestDto("newtest@example.com");
                when(emailValidationRepository.findById(anyString())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.updateEmail(1L, requestDto));
                assertEquals(ErrorStatus.EMAIL_NOT_VERIFIED, exception.getErrorStatus());
        }

        @Test
        @DisplayName("updateEmail - 가맹점을 찾을 수 없는 경우 GeneralException 발생")
        void updateEmail_merchantNotFound() {
                // Given
                MerchantEmailChangeRequestDto requestDto = new MerchantEmailChangeRequestDto("newtest@example.com");
                MerchantEmailValidation newEmailValidation = MerchantEmailValidation.builder()
                                .email("newtest@example.com")
                                .isVerified(true)
                                .build();
                when(emailValidationRepository.findById(anyString())).thenReturn(Optional.of(newEmailValidation));
                when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

                // When & Then
                GeneralException exception = assertThrows(GeneralException.class,
                                () -> merchantService.updateEmail(1L, requestDto));
                assertEquals(ErrorStatus.MERCHANT_NOT_FOUND, exception.getErrorStatus());
        }

}