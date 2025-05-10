package com.example.Tokkit_server.wallet.service.command;

import java.time.LocalDateTime;
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
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        return new WalletBalanceResponse(wallet.getDepositBalance(), wallet.getTokenBalance());
    }

    public List<TransactionHistoryResponse> getTransactionHistory(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        return transactions.stream()
            .map(t -> new TransactionHistoryResponse(
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
        Wallet wallet = walletRepository.findByUser_Id(request.getUserId()).orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        //2. 토큰 잔액확인
        if(wallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_BALANCE); // 토큰 부족
        }

        // 3. 토큰 차감
        wallet.updateBalance(wallet.getDepositBalance(), wallet.getTokenBalance() - request.getAmount());

        //  4. 바우처 엔티티 조회
        Voucher voucher = voucherRepository.findById(request.getVoucherId()).orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        // 구매 가능 수량 확인
        if (voucher.getRemainingCount() <= 0) {
            throw new GeneralException(ErrorStatus.VOUCHER_SOLD_OUT);
        }

        // 수량 차감
        voucher.decreaseRemainingCount();

        // 4. VoucherOwnership 생성
        VoucherOwnership ownership = VoucherOwnership.builder()
            .voucher(voucher)
            .remainingAmount(request.getAmount()) // 처음에는 구매 금액 전체가 남음
            .wallet(wallet)
            .status(VoucherOwnershipStatus.AVAILABLE)
            .build();

        VoucherOwnership savedOwnership = voucherOwnershipRepository.save(ownership);

        // 5.거래내역 저장
        Transaction transaction = Transaction.builder()
            .wallet(wallet)
            .type(TransactionType.PURCHASE)
            .amount(request.getAmount())
            .txHash(null) // 추후 SmartContract랑 연결하는 경우 추가 예정
            .description("바우처 구매 - Voucher ID : " + request.getVoucherId())
            .build();


        transactionRepository.save(transaction);

        // 6. 응답 반환
        return VoucherPurchaseResponse.builder()
            .ownershipId(savedOwnership.getId())
            .message("바우처 구매 완료")
            .build();
    }

    /**
     * QR 코드로 넘어온 정보 인등  & 바우처로 결제
     */
    @Transactional
    public VoucherPaymentResponse payWithVoucher(VoucherPaymentRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        // 1. 간편 비밀번호 체크
        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        //  2. 바우처 소유권 확인
        VoucherOwnership ownership = voucherOwnershipRepository.findById(request.getVoucherOwnershipId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));


        if (!ownership.getWallet().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN); // 내 소유 아님
        }

        Voucher voucher = ownership.getVoucher();

        // 3. 바우처 유효기간 확인
        if (LocalDateTime.now().isAfter(voucher.getValidDate())) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST); // 기간 만료
        }

        //  4. 사용처(merchantId) 확인
        if (!voucher.getMerchant().getId().equals(request.getMerchantId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN); // 사용처 불일치
        }

        //  5. 잔액 확인
        if (ownership.getRemainingAmount() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        //  6. 잔액 차감
        ownership.useAmount(request.getAmount());

        //  7. 거래 기록 생성
        Transaction transaction = Transaction.builder()
            .wallet(ownership.getWallet())
            .type(TransactionType.PURCHASE)
            .amount(request.getAmount())
            .txHash(null)
            .description("QR 결제 - Merchant ID: " + request.getMerchantId())
            .build();

        transactionRepository.save(transaction);

        return VoucherPaymentResponse.builder()
            .remainingAmount(ownership.getRemainingAmount())
            .message("결제 성공")
            .build();
    }

    /**
     * QR 코드로 넘어온 정보 인등  & 토큰으로 결제
     */
    @Transactional
    public DirectPaymentResponse payDirectlyWithToken(DirectPaymentRequest request) {

        // 1. 유저 조회 및 간편 비밀번호 확인
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 2. 사용자 지갑 조회
        Wallet userWallet = walletRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        // 3. 토큰 잔액 확인
        if (userWallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        // 4. 사용자 지갑 토큰 차감
        userWallet.updateBalance(
            userWallet.getDepositBalance(),
            userWallet.getTokenBalance() - request.getAmount()
        );

        // 5. 가맹점주 조회 및 지갑 조회
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        Wallet merchantWallet = walletRepository.findByMerchant_Id(merchant.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        // 6. 가맹점주 지갑 토큰 증가
        merchantWallet.updateBalance(
            merchantWallet.getDepositBalance(),
            merchantWallet.getTokenBalance() + request.getAmount()
        );

        // 7. 거래 내역 저장 (user 기준으로 기록)
        transactionRepository.save(Transaction.builder()
            .wallet(userWallet)
            .type(TransactionType.PURCHASE)
            .amount(request.getAmount())
            .description("토큰 직접 결제 - Merchant ID: " + merchant.getId())
            .build());

        // 8. 응답 반환
        return DirectPaymentResponse.builder()
            .remainingTokenBalance(userWallet.getTokenBalance())
            .message("토큰 직접 결제 성공")
            .build();
    }

}