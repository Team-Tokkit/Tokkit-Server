package com.example.Tokkit_server.wallet.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionStatus;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import com.example.Tokkit_server.transaction.service.query.TransactionLogService;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.wallet.dto.request.DepositToTokenRequest;
import com.example.Tokkit_server.wallet.dto.request.TokenToDepositRequest;
import com.example.Tokkit_server.wallet.dto.response.TransactionDetailResponse;
import com.example.Tokkit_server.wallet.dto.response.TransactionHistoryResponse;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import com.example.contract.service.TokkitTokenService;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletQueryService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionLogService transactionLogService;
    private final TokkitTokenService tokkitTokenService;

    /**
     * txHash 없는 기본형
     */
    private void logAndSave(Wallet wallet, Long userId, Long merchantId,
        TransactionType type, TransactionStatus status, Long amount, String description) {
        transactionLogService.logAndSave(
            Transaction.builder()
                .wallet(wallet)
                .type(type)
                .status(status)
                .amount(amount)
                .description(description)
                .traceId(MDC.get("traceId"))
                .build(),
            userId,
            merchantId
        );
    }

    /**
     * txHash 있는 확장형
     */
    private void logAndSave(Wallet wallet, Long userId, Long merchantId,
        TransactionType type, TransactionStatus status, Long amount, String description, String txHash) {
        transactionLogService.logAndSave(
            Transaction.builder()
                .wallet(wallet)
                .type(type)
                .status(status)
                .amount(amount)
                .txHash(txHash)
                .description(description)
                .traceId(MDC.get("traceId"))
                .build(),
            userId,
            merchantId
        );
    }


    /**
     * 예금에서 토큰으로 바꾸기
     */
    @Transactional
    public void convertDepositToToken(Long userId, DepositToTokenRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 간편 비밀번호 검증
        if (!user.matchSimplePassword(request.getSimplePassword(), passwordEncoder)) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }

        // 잔액 확인
        if (wallet.getDepositBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_BALANCE); // 잔액 부족 에러 처리
        }

        // 잔액 업데이트
        wallet.updateBalance(wallet.getDepositBalance() - request.getAmount(),
            wallet.getTokenBalance() + request.getAmount());

        TransactionReceipt receipt;
        try {
            receipt = tokkitTokenService.mint(wallet.getWalletAddress(), BigInteger.valueOf(request.getAmount()));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.TOKEN_MINT_FAILED);
        }
        String txHash = receipt.getTransactionHash();

        try {
            BigInteger onChainBalance = tokkitTokenService.getBalanceOf(wallet.getWalletAddress());
            if (!onChainBalance.equals(BigInteger.valueOf(wallet.getTokenBalance()))) {
                throw new GeneralException(ErrorStatus.BALANCE_MISMATCH);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BALANCE_VERIFICATION_FAILED);
        }

        logAndSave(wallet, user.getId(), null,
            TransactionType.CONVERT,
            TransactionStatus.SUCCESS,
            request.getAmount(),
            "예금 ➝ 토큰 변환",
            txHash);
    }


    /**
     * 토큰에서 예금으로 바꾸기
     */
    @Transactional
    public void convertTokenToDeposit(Long userId ,TokenToDepositRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 간편 비밀번호 검증
        if (!user.matchSimplePassword(request.getSimplePassword(), passwordEncoder)) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }

        // 반환 요청 잔액 비교
        if (wallet.getTokenBalance() < request.getAmount()) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_TOKEN_BALANCE); // 토큰 잔액 부족
        }


        // 스마트컨트랙트 burn
        TransactionReceipt receipt;
        try {
            receipt = tokkitTokenService.burn(wallet.getWalletAddress(), BigInteger.valueOf(request.getAmount()));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.TOKEN_BURN_FAILED);
        }

        String txHash = receipt.getTransactionHash();


        // 잔액 업데이트
        wallet.updateBalance(wallet.getDepositBalance() + request.getAmount(),
            wallet.getTokenBalance() - request.getAmount());


        try {
            BigInteger onChainBalance = tokkitTokenService.getBalanceOf(wallet.getWalletAddress());
            if (!onChainBalance.equals(BigInteger.valueOf(wallet.getTokenBalance()))) {
                throw new GeneralException(ErrorStatus.BALANCE_MISMATCH);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BALANCE_VERIFICATION_FAILED);
        }


        logAndSave(wallet, user.getId(), null,
            TransactionType.CONVERT,
            TransactionStatus.SUCCESS,
            request.getAmount(),
            "토큰 ➝ 예금 변환", txHash);
    }


    /**
     * 거래내역 조회
     */
    public List<TransactionHistoryResponse> getRecentTransactions(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_WALLET_NOT_FOUND));

        List<Transaction> transactions = transactionRepository
            .findTop10ByWalletIdOrderByCreatedAtDesc(wallet.getId());

        return transactions.stream()
            .map(t -> new TransactionHistoryResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getDescription(),
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
            transaction.getCreatedAt(),
            transaction.getTxHash()
        );
    }
}