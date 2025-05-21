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
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantWalletCommandService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLogService transactionLogService;

    private void logAndSave(Wallet wallet, Long userId, Long merchantId,
                            TransactionType type, TransactionStatus status, Long amount, String description) {
        transactionLogService.logAndSave(
                Transaction.builder()
                        .wallet(wallet)
                        .type(type)
                        .status(status)
                        .amount(amount)
                        .txHash(null)
                        .description(description)
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
                        t.getDescription(),
                        t.getCreatedAt()
                )).toList();
    }
}
