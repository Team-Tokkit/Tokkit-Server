package com.example.Tokkit_server.wallet.service.command;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.notification.service.MerchantNotificationService;
import com.example.Tokkit_server.notification.service.NotificationService;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import com.example.Tokkit_server.transaction.service.query.TransactionLogService;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.repository.VoucherRepository;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherPaymentRequest;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.wallet.dto.request.DirectPaymentRequest;
import com.example.Tokkit_server.wallet.dto.request.VoucherPurchaseRequest;
import com.example.Tokkit_server.wallet.dto.response.VoucherPaymentResponse;
import com.example.Tokkit_server.wallet.dto.response.VoucherPurchaseResponse;
import com.example.Tokkit_server.wallet.dto.response.WalletBalanceResponse;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.enums.WalletType;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import com.example.contract.service.TokkitTokenService;
import com.example.Tokkit_server.store.enums.StoreCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.example.Tokkit_server.global.entity.VoucherStore;
import com.example.Tokkit_server.notification.enums.NotificationTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalletCommandServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private VoucherOwnershipRepository voucherOwnershipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VoucherRepository voucherRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TransactionLogService transactionLogService;
    @Mock
    private TokkitTokenService tokkitTokenService;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MerchantNotificationService merchantNotificationService;

    @InjectMocks
    private WalletCommandService walletCommandService;

    private User user;
    private Merchant merchant;
    private Wallet userWallet;
    private Wallet merchantWallet;
    private Voucher voucher;
    private User anotherUser;
    private Wallet anotherUserWallet;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        user = mock(User.class);
        merchant = mock(Merchant.class);
        anotherUser = mock(User.class);
        userWallet = mock(Wallet.class);
        merchantWallet = mock(Wallet.class);
        anotherUserWallet = mock(Wallet.class);
        voucher = mock(Voucher.class);
    }

    @Test
    @DisplayName("createInitialWalletForUser - 성공적인 사용자 지갑 생성")
    void createInitialWalletForUser_Success() {
        // Given
        when(user.getId()).thenReturn(1L);
        when(walletRepository.existsByUserId(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet wallet = invocation.getArgument(0);
            return Wallet.builder()
                    .id(1L)
                    .user(wallet.getUser())
                    .depositBalance(wallet.getDepositBalance())
                    .tokenBalance(wallet.getTokenBalance())
                    .walletType(wallet.getWalletType())
                    .accountNumber(wallet.getAccountNumber())
                    .walletAddress(wallet.getWalletAddress())
                    .build();
        });

        // When
        Wallet createdWallet = walletCommandService.createInitialWalletForUser(1L);

        // Then
        assertNotNull(createdWallet);
        assertEquals(1L, createdWallet.getId());
        verify(walletRepository, times(1)).existsByUserId(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("createInitialWalletForUser - 사용자 지갑 이미 존재")
    void createInitialWalletForUser_AlreadyExists() {
        // Given
        when(walletRepository.existsByUserId(anyLong())).thenReturn(true);
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));

        // When
        Wallet existingWallet = walletCommandService.createInitialWalletForUser(user.getId());

        // Then
        assertNotNull(existingWallet);
        assertEquals(userWallet.getId(), existingWallet.getId());
        verify(walletRepository).existsByUserId(user.getId());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(userRepository, never()).findById(anyLong());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("createInitialWalletForUser - 사용자 없음 예외")
    void createInitialWalletForUser_UserNotFound() {
        // Given
        when(walletRepository.existsByUserId(anyLong())).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.createInitialWalletForUser(user.getId()));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        // verify(walletRepository).existsByUserId(user.getId());
        // verify(userRepository).findById(user.getId());
        // verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("createInitialWalletForMerchant - 성공적인 가맹점 지갑 생성")
    void createInitialWalletForMerchant_Success() {
        // Given
        when(walletRepository.existsByMerchantId(anyLong())).thenReturn(false);
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.of(merchant));
        when(walletRepository.save(any(Wallet.class))).thenReturn(merchantWallet);

        // When
        Wallet createdWallet = walletCommandService.createInitialWalletForMerchant(merchant.getId());

        // Then
        assertNotNull(createdWallet);
        // assertEquals(merchant.getId(), createdWallet.getMerchant().get.Id());
        verify(walletRepository).existsByMerchantId(merchant.getId());
        verify(merchantRepository).findById(merchant.getId());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("createInitialWalletForMerchant - 가맹점 지갑 이미 존재")
    void createInitialWalletForMerchant_AlreadyExists() {
        // Given
        when(walletRepository.existsByMerchantId(anyLong())).thenReturn(true);
        when(walletRepository.findByMerchant_Id(anyLong())).thenReturn(Optional.of(merchantWallet));

        // When
        Wallet existingWallet = walletCommandService.createInitialWalletForMerchant(merchant.getId());

        // Then
        assertNotNull(existingWallet);
        assertEquals(merchantWallet.getId(), existingWallet.getId());
        verify(walletRepository).existsByMerchantId(merchant.getId());
        verify(walletRepository).findByMerchant_Id(merchant.getId());
        verify(merchantRepository, never()).findById(anyLong());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("createInitialWalletForMerchant - 가맹점 없음 예외")
    void createInitialWalletForMerchant_MerchantNotFound() {
        // Given
        when(walletRepository.existsByMerchantId(anyLong())).thenReturn(false);
        when(merchantRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.createInitialWalletForMerchant(merchant.getId()));
        assertEquals(ErrorStatus.MERCHANT_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).existsByMerchantId(merchant.getId());
        verify(merchantRepository).findById(merchant.getId());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("getWalletBalance - 성공적인 지갑 잔액 조회")
    void getWalletBalance_Success() {
        // Given
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(userWallet.getDepositBalance()).thenReturn(1000000L);
        when(userWallet.getTokenBalance()).thenReturn(5000L);
        when(userWallet.getUser()).thenReturn(user);
        when(user.getName()).thenReturn("테스트유저");
        when(userWallet.getAccountNumber()).thenReturn("ACC123");

        // When
        WalletBalanceResponse response = walletCommandService.getWalletBalance(user.getId());

        // Then
        assertNotNull(response);
        assertEquals(1000000L, response.getDepositBalance());
        assertEquals(5000L, response.getTokenBalance());
        assertEquals("테스트유저", response.getName());
        assertEquals("ACC123", response.getAccountNumber());
        verify(walletRepository).findByUser_Id(user.getId());
    }

    @Test
    @DisplayName("getWalletBalance - 사용자 지갑 없음 예외")
    void getWalletBalance_UserWalletNotFound() {
        // Given
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.getWalletBalance(user.getId()));
        assertEquals(ErrorStatus.USER_WALLET_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
    }

    @Test
    @DisplayName("getTransactionHistory - 성공적인 거래 내역 조회")
    void getTransactionHistory_Success() {
        // Given
        Transaction transaction = Transaction.builder()
                .id(1L)
                .wallet(userWallet)
                .type(TransactionType.DEPOSIT)
                .amount(1000L)
                .displayDescription("Test Deposit")
                .build();

        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(transactionRepository.findByWalletIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(Collections.singletonList(transaction));

        // When
        var response = walletCommandService.getTransactionHistory(user.getId());

        // Then
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(1000L, response.get(0).getAmount());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(transactionRepository).findByWalletIdOrderByCreatedAtDesc(userWallet.getId());
    }

    @Test
    @DisplayName("getTransactionHistory - 사용자 지갑 없음 예외")
    void getTransactionHistory_UserWalletNotFound() {
        // Given
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.getTransactionHistory(user.getId()));
        assertEquals(ErrorStatus.USER_WALLET_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(transactionRepository, never()).findByWalletIdOrderByCreatedAtDesc(anyLong());
    }

    // @Test
    // @DisplayName("purchaseVoucher - 성공적인 바우처 구매")
    // void purchaseVoucher_Success() throws Exception {
    // // given
    // Long userId = 1L;
    // Long voucherId = 1L;
    // String simplePassword = "1234";
    // int amount = 10000;

    // User user = mock(User.class);
    // Wallet userWallet = mock(Wallet.class);
    // Voucher voucher = mock(Voucher.class);
    // VoucherOwnership ownership = mock(VoucherOwnership.class);
    // TransactionReceipt receipt = mock(TransactionReceipt.class);

    // when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    // when(walletRepository.findByUser_Id(userId)).thenReturn(Optional.of(userWallet));
    // when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));
    // when(user.matchSimplePassword(simplePassword,
    // passwordEncoder)).thenReturn(true);
    // when(voucher.getPrice()).thenReturn(amount);
    // when(voucher.getRemainingCount()).thenReturn(1);
    // when(userWallet.getTokenBalance()).thenReturn((long) amount);
    // when(userWallet.getWalletAddress()).thenReturn("0x123");
    // when(tokkitTokenService.burn(anyString(),
    // any(BigInteger.class))).thenReturn(receipt);
    // when(receipt.getTransactionHash()).thenReturn("0xabc");
    // when(voucherOwnershipRepository.save(any(VoucherOwnership.class))).thenReturn(ownership);
    // when(ownership.getId()).thenReturn(1L);

    // VoucherPurchaseRequest request = VoucherPurchaseRequest.builder()
    // .voucherId(voucherId)
    // .simplePassword(simplePassword)
    // .build();

    // // when
    // VoucherPurchaseResponse response =
    // walletCommandService.purchaseVoucher(userId, request);

    // // then
    // assertNotNull(response);
    // assertEquals(1L, response.getOwnershipId());
    // assertEquals("바우처 구매 완료", response.getMessage());

    // verify(userRepository).findById(userId);
    // verify(walletRepository).findByUser_Id(userId);
    // verify(voucherRepository).findById(voucherId);
    // verify(user).matchSimplePassword(simplePassword, passwordEncoder);
    // verify(voucher).getPrice();
    // verify(voucher).getRemainingCount();
    // verify(voucher).decreaseRemainingCount();

    // // FIXED: Changed from times(1) to times(2) since getTokenBalance() is called
    // // twice
    // verify(userWallet, times(2)).getTokenBalance();

    // verify(userWallet).getWalletAddress();
    // verify(tokkitTokenService).burn(anyString(), any(BigInteger.class));
    // verify(userWallet).updateBalance(anyLong(), eq((long) (amount * -1)));
    // verify(voucherOwnershipRepository).save(any(VoucherOwnership.class));
    // }

    @Test
    @DisplayName("purchaseVoucher - 사용자 지갑 없음 예외")
    void purchaseVoucher_UserWalletNotFound() {
        // Given
        VoucherPurchaseRequest request = new VoucherPurchaseRequest(1L, "1234");
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.purchaseVoucher(user.getId(), request));
        assertEquals(ErrorStatus.USER_WALLET_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("purchaseVoucher - 사용자 없음 예외")
    void purchaseVoucher_UserNotFound() {
        // Given
        VoucherPurchaseRequest request = new VoucherPurchaseRequest(1L, "1234");
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.purchaseVoucher(user.getId(), request));
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(userRepository).findById(user.getId());
        verify(user, never()).matchSimplePassword(anyString(), any(PasswordEncoder.class));
    }

    @Test
    @DisplayName("purchaseVoucher - 간편 비밀번호 불일치 예외")
    void purchaseVoucher_InvalidSimplePassword() {
        // Given
        VoucherPurchaseRequest request = new VoucherPurchaseRequest(1L, "wrong");
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.matchSimplePassword(anyString(), any(PasswordEncoder.class))).thenReturn(false);

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.purchaseVoucher(user.getId(), request));
        assertEquals(ErrorStatus.INVALID_SIMPLE_PASSWORD, exception.getErrorStatus());
        // verify(walletRepository).findByUser_Id(user.getId());
        // verify(userRepository).findById(user.getId());
        // verify(user).matchSimplePassword("wrong", passwordEncoder);
        // verify(voucherRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("purchaseVoucher - 바우처 없음 예외")
    void purchaseVoucher_VoucherNotFound() {
        // Given
        VoucherPurchaseRequest request = new VoucherPurchaseRequest(1L, "1234");
        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.matchSimplePassword(anyString(), any(PasswordEncoder.class))).thenReturn(true);
        when(voucherRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.purchaseVoucher(user.getId(), request));
        assertEquals(ErrorStatus.VOUCHER_NOT_FOUND, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(userRepository).findById(user.getId());
        verify(user).matchSimplePassword("1234", passwordEncoder);
        verify(voucherRepository).findById(1L);
    }

    @Test
    @DisplayName("purchaseVoucher - 토큰 잔액 부족 예외")
    void purchaseVoucher_InsufficientTokenBalance() {
        // Given
        VoucherPurchaseRequest request = new VoucherPurchaseRequest(1L, "1234");
        Voucher expensiveVoucher = Voucher.builder()
                .id(1L)
                .price(6000)
                .remainingCount(10)
                .build();

        when(walletRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(userWallet));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(user.matchSimplePassword(anyString(), any(PasswordEncoder.class))).thenReturn(true);
        when(voucherRepository.findById(anyLong())).thenReturn(Optional.of(expensiveVoucher));

        // When & Then
        GeneralException exception = assertThrows(GeneralException.class,
                () -> walletCommandService.purchaseVoucher(user.getId(), request));
        assertEquals(ErrorStatus.INSUFFICIENT_TOKEN_BALANCE, exception.getErrorStatus());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(userRepository).findById(user.getId());
        verify(user).matchSimplePassword("1234", passwordEncoder);
        verify(voucherRepository).findById(1L);
    }

    @Test
    @DisplayName("payWithVoucher - 성공적인 바우처 결제")
    void payWithVoucher_Success() throws Exception {
        // Given
        // 1. 기본 mock 객체 생성
        VoucherOwnership voucherOwnership = mock(VoucherOwnership.class);
        Store store = mock(Store.class);
        VoucherStore voucherStore = mock(VoucherStore.class);
        TransactionReceipt receipt = mock(TransactionReceipt.class);

        // 2. mock 객체들의 getter 설정
        // voucherOwnership 설정
        doReturn(1L).when(voucherOwnership).getId();
        doReturn(userWallet).when(voucherOwnership).getWallet();
        doReturn(voucher).when(voucherOwnership).getVoucher();
        doReturn(VoucherOwnershipStatus.AVAILABLE).when(voucherOwnership).getStatus();
        doReturn(2000L).when(voucherOwnership).getRemainingAmount();

        // store 설정
        doReturn(1L).when(store).getId();
        doReturn("테스트가게").when(store).getStoreName();
        doReturn(merchant).when(store).getMerchant();

        // voucherStore 설정
        doReturn(store).when(voucherStore).getStore();
        doReturn(voucher).when(voucherStore).getVoucher();

        // user 설정
        doReturn(1L).when(user).getId();
        doReturn(true).when(user).matchSimplePassword(anyString(), any(PasswordEncoder.class));

        // userWallet 설정 - 중요: getUser() 메서드 추가
        doReturn(user).when(userWallet).getUser();

        // voucher 설정
        doReturn(LocalDateTime.now().plusDays(1)).when(voucher).getValidDate();
        doReturn(merchant).when(voucher).getMerchant();
        doReturn(Collections.singletonList(voucherStore)).when(voucher).getVoucherStores();

        // merchant 설정
        doReturn(1L).when(merchant).getId();

        // merchantWallet 설정
        doReturn(10000L).when(merchantWallet).getDepositBalance();
        doReturn(10000L).when(merchantWallet).getTokenBalance();
        doReturn("0xMerchantWallet").when(merchantWallet).getWalletAddress();

        // receipt 설정
        doReturn("0xhash").when(receipt).getTransactionHash();

        // 3. repository mock 설정
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(Optional.of(voucherOwnership)).when(voucherOwnershipRepository).findById(anyLong());
        doReturn(Optional.of(store)).when(storeRepository).findById(anyLong());
        doReturn(Optional.of(merchantWallet)).when(walletRepository).findByMerchant_Id(anyLong());
        doReturn(Optional.of(merchant)).when(merchantRepository).findById(anyLong());

        // 4. service mock 설정
        doReturn(receipt).when(tokkitTokenService).mint(anyString(), any(BigInteger.class));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any());
        doNothing().when(merchantNotificationService).sendMerchantNotification(any(), any(), any(), any(), any());

        // 5. request 생성
        VoucherPaymentRequest request = new VoucherPaymentRequest(
                voucherOwnership.getId(), // 바우처 소유권 ID
                merchant.getId(), // 가맹점 ID
                store.getId(), // 매장 ID
                1000L, // 결제 금액
                "1234" // 간편 비밀번호
        );

        // When
        VoucherPaymentResponse response = walletCommandService.payWithVoucher(user.getId(), request);

        // Then
        assertNotNull(response);
        assertEquals("결제 성공", response.getMessage());
        verify(userRepository).findById(anyLong());
        verify(voucherOwnershipRepository).findById(anyLong());
        verify(user).matchSimplePassword(anyString(), any(PasswordEncoder.class));
        verify(storeRepository).findById(anyLong());
        verify(walletRepository).findByMerchant_Id(anyLong());
        verify(tokkitTokenService).mint(anyString(), any(BigInteger.class));

        // FIXED: Changed from times(2) to times(1) since it's only called once
        verify(merchantRepository, times(1)).findById(merchant.getId());

        verify(walletRepository).findByMerchant_Id(merchant.getId());
        verify(storeRepository).findById(store.getId());
        verify(notificationService).sendNotification(
                any(User.class),
                eq(NotificationTemplate.VOUCHER_PAYMENT_SUCCESS),
                isNull(),
                eq("테스트가게"),
                eq(1000L));
        verify(merchantNotificationService).sendMerchantNotification(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("payDirectlyWithToken - 성공적인 토큰 직접 결제")
    void payDirectlyWithToken_Success() throws Exception {
        // Given
        DirectPaymentRequest request = new DirectPaymentRequest("1234", 1L, 1000L);

        // Mock 객체 생성
        User user = mock(User.class);
        Wallet userWallet = mock(Wallet.class);
        Merchant merchant = mock(Merchant.class);
        Wallet merchantWallet = mock(Wallet.class);
        Store store = mock(Store.class);
        TransactionReceipt receipt = mock(TransactionReceipt.class);

        // user, merchant, store id
        doReturn(1L).when(user).getId();
        doReturn(1L).when(merchant).getId();
        doReturn(1L).when(store).getId();
        doReturn(merchant).when(store).getMerchant();
        doReturn("테스트가게").when(store).getStoreName();

        // userWallet, merchantWallet
        doReturn(10_000L).when(userWallet).getTokenBalance();
        doReturn(10_000L).when(userWallet).getDepositBalance();
        doReturn(10_000L).when(merchantWallet).getTokenBalance();
        doReturn(10_000L).when(merchantWallet).getDepositBalance();
        doReturn("0xUserWallet").when(userWallet).getWalletAddress();
        doReturn("0xMerchantWallet").when(merchantWallet).getWalletAddress();

        // 비밀번호 일치
        doReturn(true).when(user).matchSimplePassword(anyString(), any(PasswordEncoder.class));

        // TransactionReceipt
        doReturn("0xhash").when(receipt).getTransactionHash();

        // repository mock
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(Optional.of(userWallet)).when(walletRepository).findByUser_Id(anyLong());
        doReturn(Optional.of(merchant)).when(merchantRepository).findById(anyLong());
        doReturn(Optional.of(merchantWallet)).when(walletRepository).findByMerchant_Id(anyLong());
        doReturn(Optional.of(store)).when(storeRepository).findByMerchantId(anyLong());

        // service mock
        doReturn(receipt).when(tokkitTokenService).transfer(anyString(), any(BigInteger.class));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any());
        doNothing().when(merchantNotificationService).sendMerchantNotification(any(), any(), any(), any());

        // When
        var response = walletCommandService.payDirectlyWithToken(user.getId(), request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getPaymentTime());
        assertEquals(1000L, response.getAmount());
        assertEquals("토큰 직접 결제 성공", response.getMessage());
        verify(userRepository).findById(user.getId());
        verify(walletRepository).findByUser_Id(user.getId());
        verify(merchantRepository, times(2)).findById(merchant.getId()); // 2번 호출됨을 명시
        verify(walletRepository).findByMerchant_Id(merchant.getId());
        verify(storeRepository).findByMerchantId(merchant.getId());
        verify(tokkitTokenService).transfer(anyString(), any(BigInteger.class));
        verify(notificationService).sendNotification(any(), any(), any(), any());
        verify(merchantNotificationService).sendMerchantNotification(any(), any(), any(), any());
    }
}