package com.example.Tokkit_server.wallet.service.command;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionStatus;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import com.example.Tokkit_server.transaction.service.query.TransactionLogService;
import com.example.Tokkit_server.wallet.dto.response.MerchantWalletBalanceResponse;
import com.example.Tokkit_server.wallet.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import com.example.contract.service.TokkitTokenService;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantWalletCommandService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
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
     * 지갑 잔액 조회
     */
    public MerchantWalletBalanceResponse getWalletBalance(Long merchantId) {
        Wallet wallet = walletRepository.findByMerchant_Id(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));

        return new MerchantWalletBalanceResponse(wallet.getDepositBalance(), wallet.getTokenBalance(), wallet.getMerchant().getStore().getStoreName(), wallet.getAccountNumber());
    }


    /**
     * 일일 매출 조회
     */
    public Long getDailyIncome(Long merchantId) {
        Optional<Wallet> wallet = walletRepository.findByMerchantId(merchantId);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return transactionRepository.findTodayRevenueByWalletId(wallet.get().getId(), startOfDay, endOfDay);
    }

    /**
     * 전체 거래내역 조회
     */

    public List<TransactionHistoryResponse> getTransactionHistory(Long merchantId) {
        Wallet wallet = walletRepository.findByMerchant_Id(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_WALLET_NOT_FOUND));

        List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        return transactions.stream()
                .map(t -> new TransactionHistoryResponse(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getDisplayDescription(),
                        t.getCreatedAt()
                )).toList();
    }
}
