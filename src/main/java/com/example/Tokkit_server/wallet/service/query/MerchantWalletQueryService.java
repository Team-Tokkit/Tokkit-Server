package com.example.Tokkit_server.wallet.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionStatus;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import com.example.Tokkit_server.transaction.service.query.TransactionLogService;
import com.example.Tokkit_server.wallet.dto.request.TokenToDepositRequest;
import com.example.Tokkit_server.wallet.dto.response.TransactionDetailResponse;
import com.example.Tokkit_server.wallet.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantWalletQueryService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionLogService transactionLogService;

    private void logAndSave(Wallet wallet, Long userId, Long merchantId,
                            TransactionType type, TransactionStatus status, Long amount, String description, String displayDescription) {
        transactionLogService.logAndSave(
                Transaction.builder()
                        .wallet(wallet)
                        .type(type)
                        .status(status)
                        .amount(amount)
                        .txHash(null)
                        .description(description)
                        .displayDescription(displayDescription)
                        .traceId(MDC.get("traceId"))
                        .build(),
                userId,
                merchantId
        );
    }

    /**
     * 토큰에서 예금으로 바꾸기
     */
    @Transactional
    public void convertTokenToDeposit(Long merchantId, TokenToDepositRequest request) {
        Wallet wallet = walletRepository.findByMerchant_Id(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));

        // 사용자 조회
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        // 간편 비밀번호 검증
        if (!merchant.matchSimplePassword(passwordEncoder, request.getSimplePassword())) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }

        // 반환 요청 잔액 비교
        if (wallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_TOKEN_BALANCE); // 토큰 잔액 부족
        }

        // 잔액 업데이트
        wallet.updateBalance(wallet.getDepositBalance() + request.getAmount(),
                wallet.getTokenBalance() - request.getAmount());

        logAndSave(wallet, merchant.getId(), null,
                TransactionType.CONVERT,
                TransactionStatus.SUCCESS,
                request.getAmount(),
                "토큰 ➝ 예금 변환",
                "토큰 ➝ 예금"
                );
    }

    /**
     * 거래내역 조회
     */
    public List<TransactionHistoryResponse> getRecentTransactions(Long merchant) {
        Wallet wallet = walletRepository.findByMerchant_Id(merchant)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));

        List<Transaction> transactions = transactionRepository
                .findTop10ByWalletIdOrderByCreatedAtDesc(wallet.getId());

        return transactions.stream()
                .map(t -> new TransactionHistoryResponse(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getDisplayDescription(),
                        t.getCreatedAt()))
                .toList();
    }


    /**
     * 거래상세내역 조회
     */
    public TransactionDetailResponse getTransactionDetail(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TRANSACTION_NOT_FOUND));
        return new TransactionDetailResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDisplayDescription(),
                transaction.getCreatedAt()
        );
    }
}
