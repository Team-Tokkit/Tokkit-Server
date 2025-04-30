package com.example.Tokkit_server.service.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Tokkit_server.Enum.TransactionType;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Transaction;
import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.domain.Wallet;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.dto.request.VoucherPaymentRequest;
import com.example.Tokkit_server.dto.request.VoucherPurchaseRequest;
import com.example.Tokkit_server.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.dto.response.VoucherPaymentResponse;
import com.example.Tokkit_server.dto.response.VoucherPurchaseResponse;
import com.example.Tokkit_server.dto.response.WalletBalanceResponse;
import com.example.Tokkit_server.repository.TransactionRepository;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletCommandService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final UserRepository userRepository;

    // 전자 지갑 생성
    @Transactional
    public void createInitialWallet(Long userId) {
        // 이미 전자지갑이 있는지 확인
        if (walletRepository.existsByUserId(userId)) {
            return;
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Wallet wallet = Wallet.builder()
            .user(user)
            .depositBalance(0L)
            .tokenBalance(0L)
            .build();

        walletRepository.save(wallet);
    }

    public WalletBalanceResponse getWalletBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        return new WalletBalanceResponse(wallet.getDepositBalance(), wallet.getTokenBalance());
    }

    public List<TransactionHistoryResponse> getTransactionHistory(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
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

    @Transactional
    public VoucherPurchaseResponse purchaseVoucher(VoucherPurchaseRequest request) {
        // 1. 사용자 Wallet 조회
        Wallet wallet = walletRepository.findByUserId(request.getUserId()).orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        //2. 토큰 잔액확인
        if(wallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST); // 토큰 부족
        }

        // 3. 토큰 차감
        wallet.updateBalance(wallet.getDepositBalance(), wallet.getTokenBalance() - request.getAmount());


        // 4. VoucherOwnership 생성
        VoucherOwnership ownership = VoucherOwnership.builder()
            .userId(request.getUserId())
            .voucherId(request.getVoucherId())
            .remainingAmount(request.getAmount()) // 처음에는 구매 금액 전체가 남음
            .wallet(wallet)
            .isUsed(false)
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

        return new VoucherPurchaseResponse(savedOwnership.getId(), "바우처 구매 완료");
    }

    @Transactional
    public VoucherPaymentResponse payWithVoucher(VoucherPaymentRequest request) {
        VoucherOwnership ownership = voucherOwnershipRepository.findById(request.getVoucherOwnershipId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        if (!ownership.getUserId().equals(request.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        if (ownership.getRemainingAmount() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST); // 잔액 부족
        }

        // 남은 금액 차감
        ownership.useAmount(request.getAmount());

        // 거래 내역 저장
        Transaction transaction = Transaction.builder()
            .wallet(ownership.getWallet())
            .type(com.example.Tokkit_server.Enum.TransactionType.PURCHASE) // Enum 타입 사용
            .amount(request.getAmount())
            .txHash(null) // 토큰 거래가 아니니까 null
            .description("바우처 사용 - Merchant ID: " + request.getMerchantId())
            .build();

        transactionRepository.save(transaction);

        return new VoucherPaymentResponse(ownership.getRemainingAmount(), "결제 성공");
    }

}