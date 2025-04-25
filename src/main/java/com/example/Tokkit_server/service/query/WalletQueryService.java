package com.example.Tokkit_server.service.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Transaction;
import com.example.Tokkit_server.domain.Wallet;
import com.example.Tokkit_server.dto.request.DepositToTokenRequest;
import com.example.Tokkit_server.dto.request.TokenToDepositRequest;
import com.example.Tokkit_server.dto.response.TransactionDetailResponse;
import com.example.Tokkit_server.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.repository.TransactionRepository;
import com.example.Tokkit_server.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletQueryService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void convertDepositToToken(DepositToTokenRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        if (wallet.getDepositBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        wallet.updateBalance(wallet.getDepositBalance() - request.getAmount(),
                               wallet.getTokenBalance() + request.getAmount());
    }

    @Transactional
    public void convertTokenToDeposit(TokenToDepositRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        if (wallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        wallet.updateBalance(wallet.getDepositBalance() + request.getAmount(),
                               wallet.getTokenBalance() - request.getAmount());
    }

    public List<TransactionHistoryResponse> getRecentTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findTop10ByWalletUserIdOrderByCreatedAtDesc(userId);
        return transactions.stream()
            .map(t -> new TransactionHistoryResponse(t.getType(), t.getAmount(), t.getDescription(), t.getCreatedAt()))
            .toList();
    }

    public TransactionDetailResponse getTransactionDetail(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
        return new TransactionDetailResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }

}