package com.example.Tokkit_server.service.command;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Transaction;
import com.example.Tokkit_server.domain.Wallet;
import com.example.Tokkit_server.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.dto.response.WalletBalanceResponse;
import com.example.Tokkit_server.repository.TransactionRepository;
import com.example.Tokkit_server.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletCommandService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;


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

}