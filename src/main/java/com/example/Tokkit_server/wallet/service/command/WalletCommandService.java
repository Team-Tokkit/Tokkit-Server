package com.example.Tokkit_server.wallet.service.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.Tokkit_server.wallet.enums.WalletType;
import com.example.Tokkit_server.wallet.utils.AccountGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.example.Tokkit_server.wallet.dto.request.DirectPaymentRequest;
import com.example.Tokkit_server.wallet.dto.response.DirectPaymentResponse;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.voucher.repository.VoucherRepository;
import com.example.Tokkit_server.wallet.dto.response.PaymentOptionResponse;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherPaymentRequest;
import com.example.Tokkit_server.wallet.dto.request.VoucherPurchaseRequest;
import com.example.Tokkit_server.wallet.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.wallet.dto.response.VoucherPaymentResponse;
import com.example.Tokkit_server.wallet.dto.response.VoucherPurchaseResponse;
import com.example.Tokkit_server.wallet.dto.response.WalletBalanceResponse;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletCommandService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final MerchantRepository merchantRepository;

    // 유저 - 전자 지갑 생성
    @Transactional
    public Wallet createInitialWalletForUser(Long userId) {
        if (walletRepository.existsByUserId(userId)) {
            return walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Wallet wallet = Wallet.builder()
                .user(user)
                .depositBalance(0L)
                .tokenBalance(0L)
                .walletType(WalletType.USER)
                .accountNumber(AccountGenerator.generateAccountNumber())
                .build();

        return walletRepository.save(wallet);
    }

    // 가맹점주 - 전자 지갑 생성
    @Transactional
    public Wallet createInitialWalletForMerchant(Long merchantId) {
        if (walletRepository.existsByMerchantId(merchantId)) {
            return walletRepository.findByMerchantId(merchantId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));
        }

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        Wallet wallet = Wallet.builder()
                .merchant(merchant)
                .depositBalance(0L)
                .tokenBalance(0L)
                .walletType(WalletType.MERCHANT)
                .accountNumber(AccountGenerator.generateAccountNumber())
                .build();

        return walletRepository.save(wallet);
    }

    /**
     * 지갑 잔액 조회
     */
    public WalletBalanceResponse getWalletBalance(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        return new WalletBalanceResponse(wallet.getDepositBalance(), wallet.getTokenBalance(), wallet.getUser().getName(), wallet.getAccountNumber());
    }

    public List<TransactionHistoryResponse> getTransactionHistory(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        return transactions.stream()
            .map(t -> new TransactionHistoryResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getDescription(),
                t.getCreatedAt()
            )).toList();
    }

    /**
     * 토큰으로 바우처 구입
     */
    @Transactional
    public VoucherPurchaseResponse purchaseVoucher(VoucherPurchaseRequest request) {
        // 1. 사용자 Wallet 조회
        Wallet wallet = walletRepository.findByUser_Id(request.getUserId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        // 2. 바우처 엔티티 조회
        Voucher voucher = voucherRepository.findById(request.getVoucherId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        int amount = voucher.getPrice(); // 서버에서 금액 조회

        // 3. 토큰 잔액확인
        if (wallet.getTokenBalance() < amount) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_TOKEN_BALANCE); // 토큰 부족
        }

        // 4. 바우처 수량 확인 및 차감
        if (voucher.getRemainingCount() <= 0) {
            throw new GeneralException(ErrorStatus.VOUCHER_SOLD_OUT);
        }

        // 수량 차감
        voucher.decreaseRemainingCount();

        // 5. 토큰 차감
        wallet.updateBalance(wallet.getDepositBalance(), wallet.getTokenBalance() - amount);

        // 6. VoucherOwnership(바우처 소유권) 생성
        VoucherOwnership ownership = VoucherOwnership.builder()
            .voucher(voucher)
            .remainingAmount((long) amount)
            .wallet(wallet)
            .status(VoucherOwnershipStatus.AVAILABLE)
            .build();

        VoucherOwnership savedOwnership = voucherOwnershipRepository.save(ownership);

        // 7. 거래내역 저장
        Transaction transaction = Transaction.builder()
            .wallet(wallet)
            .type(TransactionType.PURCHASE)
            .amount((long) amount)
            .txHash(null)
            .description("바우처 구매 - Voucher ID: " + voucher.getId() + ", 금액: " + amount + "원") // 금액 표시
            .build();
        transactionRepository.save(transaction);

        // 8. 응답 반환
        return VoucherPurchaseResponse.builder()
            .ownershipId(savedOwnership.getId())
            .message("바우처 구매 완료")
            .build();
    }


    /**
     * QR 코드로 넘어온 정보 인증  & 바우처로 결제
     */
    @Transactional
    public VoucherPaymentResponse payWithVoucher(VoucherPaymentRequest request) {

        //  1. 사용자 조회
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        //  2. 바우처 소유권 확인
        VoucherOwnership ownership = voucherOwnershipRepository.findById(request.getVoucherOwnershipId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_OWNERSHIP_NOT_FOUND));


        if (!ownership.getWallet().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.VOUCHER_NOT_OWNED_BY_USER); // 내 소유 아님
        }

        Voucher voucher = ownership.getVoucher();

        // 3. 바우처 유효기간 확인
        if (LocalDateTime.now().isAfter(voucher.getValidDate())) {
            throw new GeneralException(ErrorStatus.VOUCHER_EXPIRED); // 기간 만료
        }

        //  4. 사용처(merchantId) 확인
        if (!voucher.getMerchant().getId().equals(request.getMerchantId())) {
            throw new GeneralException(ErrorStatus.VOUCHER_MERCHANT_NOT_MATCH); // 바우처 사용처 불일치
        }

        //  5. 바우처 사용 가능 매장인지 확인
        boolean usableStore = voucher.getVoucherStores().stream()
            .anyMatch(vs -> vs.getStore().getId().equals(request.getStoreId()));

        if (!usableStore) {
            throw new GeneralException(ErrorStatus.VOUCHER_STORE_NOT_USABLE);
        }

        //  6. 잔액 확인
        if (ownership.getRemainingAmount() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_BALANCE);
        }

        // 7. 간편 비밀번호 체크
        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }

        //  8. 잔액 차감
        ownership.useAmount(request.getAmount());

        //  9. 사용자 거래 기록 생성
        Transaction userTX = Transaction.builder()
            .wallet(ownership.getWallet())
            .type(TransactionType.PURCHASE)
            .amount(request.getAmount())
            .txHash(null)
            .description("QR 바우처 결제 - Merchant ID: " + request.getMerchantId())
            .build();

        transactionRepository.save(userTX);


        //  10. 가맹점주 Wallet 정산
        Wallet merchantWallet = walletRepository.findByMerchant_Id(request.getMerchantId()).orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));

        merchantWallet.updateBalance(
            merchantWallet.getDepositBalance(),
            merchantWallet.getTokenBalance() + request.getAmount());


        //  11. 가맹점주 거래 기록 저장
         Transaction merchantTX = Transaction.builder()
             .wallet(merchantWallet)
             .type(TransactionType.RECEIVE)
             .amount(request.getAmount())
             .txHash(null)
             .description("고객 바우처 결제 정산 - UserID: " + user.getId())
             .build();

         transactionRepository.save(merchantTX);

         //  12. 응답 반환
        return VoucherPaymentResponse.builder()
            .paymentTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME))
            .amount(request.getAmount())
            .remainingAmount(ownership.getRemainingAmount())
            .message("결제 성공")
            .build();
    }


    /**
     * QR 코드로 넘어온 정보 인증  & 토큰으로 결제
     */
    @Transactional
    public DirectPaymentResponse payDirectlyWithToken(DirectPaymentRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        // 2. 사용자 지갑 조회
        Wallet userWallet = walletRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        // 3. 토큰 잔액 확인
        if (userWallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_TOKEN_BALANCE);
        }


        // 4. 가맹점주 조회 및 지갑 조회
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        Wallet merchantWallet = walletRepository.findByMerchant_Id(merchant.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));


        // 5. 간편 비밀번호  검증
        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }

        // 6. 사용자 토큰 차감
        userWallet.updateBalance(
            userWallet.getDepositBalance(),
            userWallet.getTokenBalance() - request.getAmount()
        );


        // 7. 가맹점주 토큰 증가
        merchantWallet.updateBalance(
            merchantWallet.getDepositBalance(),
            merchantWallet.getTokenBalance() + request.getAmount()
        );


        // 8. 유저 거래 내역 저장
        transactionRepository.save(Transaction.builder()
            .wallet(userWallet)
            .type(TransactionType.PURCHASE)
            .amount(request.getAmount())
            .description("토큰 직접 결제 - Merchant ID: " + merchant.getId())
            .build());


        // 9. 가맹점주 거래 기록 저장
        transactionRepository.save(Transaction.builder()
            .wallet(merchantWallet)
            .type(TransactionType.RECEIVE) // RECEIVE 타입 없으면 추가하거나 PURCHASE 재사용
            .amount(request.getAmount())
            .description("토큰 직접 결제 정산 수령 - From User ID: " + user.getId())
            .build());

        // 10. 응답 반환
        return DirectPaymentResponse.builder()
            .paymentTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME))
            .amount(request.getAmount())
            .remainingTokenBalance(userWallet.getTokenBalance())
            .message("토큰 직접 결제 성공")
            .build();
    }


    public List<PaymentOptionResponse> getPaymentOptions(Long userId, Long storeId) {
        List<PaymentOptionResponse> result = new ArrayList<>();

        // 1. 사용자 지갑의 토큰 잔액
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        result.add(PaymentOptionResponse.builder()
            .type("TOKEN")
            .name("토큰으로 결제")
            .balance(wallet.getTokenBalance())
            .expireDate(null)
            .usable(true)
            .storeCategory("TOKEN") // 토큰은 별도 카테고리
            .build());

        // 2. 바우처 소유권 조회
        List<VoucherOwnership> ownerships = voucherOwnershipRepository.findAllWithVoucherAndStoresByUserId(userId);
       // 해당 userId가 소유한 지갑에 연결된 모든 바우처 소유권(VoucherOwnership)을 가져와라

        for (VoucherOwnership o : ownerships) {
            Voucher v = o.getVoucher();

            boolean usableStore = v.getVoucherStores().stream()
                .anyMatch(vs -> vs.getStore().getId().equals(storeId));

            if (!usableStore || v.getValidDate().isBefore(LocalDateTime.now()) || o.getRemainingAmount() <= 0) {
                continue;
            }

            result.add(PaymentOptionResponse.builder()
                .type("VOUCHER")
                .voucherOwnershipId(o.getId())
                .name(v.getName())
                .balance(o.getRemainingAmount())
                .expireDate(v.getValidDate().toLocalDate().toString())
                .usable(true)
                .storeCategory(v.getStoreCategory().name())
                .build());
        }

        return result;
    }

}