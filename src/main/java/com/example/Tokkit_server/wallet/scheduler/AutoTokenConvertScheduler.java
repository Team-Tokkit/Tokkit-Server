package com.example.Tokkit_server.wallet.scheduler;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionStatus;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.service.query.TransactionLogService;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import com.example.contract.service.TokkitTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoTokenConvertScheduler {

    private final WalletRepository walletRepository;
    private final TokkitTokenService tokkitTokenService;
    private final TransactionLogService transactionLogService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * txHash 있는 확장형
     */
    private void logAndSave(Wallet wallet, Long userId, Long merchantId,
        TransactionType type, TransactionStatus status, Long amount, String description, String displayDescription, String txHash) {

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        transactionLogService.logAndSave(
            Transaction.builder()
                .wallet(wallet)
                .type(type)
                .status(status)
                .amount(amount)
                .txHash(txHash)
                .description(description)
                .displayDescription(displayDescription)
                .traceId(traceId)
                .build(),
            userId,
            merchantId
        );
    }

    @Scheduled(fixedRate = 60000) // 매 분 실행 (스프링 쪽에서 이걸 스캔한다는 뜻)
    @Transactional
    public void runMonthlyAutoConversion() {
        MDC.put("traceId", UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now();
        int nowDay = now.getDayOfMonth();
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();

        List<Wallet> wallets = walletRepository.findByAutoConvertEnabledTrue();

        for (Wallet wallet : wallets) {
            if (!wallet.isAutoConvertEnabled()) continue;
            if (!Objects.equals(wallet.getAutoConvertDayOfMonth(), nowDay)) continue;
            if (!Objects.equals(wallet.getAutoConvertHour(), nowHour)) continue;
            if (!Objects.equals(wallet.getAutoConvertMinute(), nowMinute)) continue;

            String lockKey = "auto-convert-lock:" + wallet.getId();
            // Redis 락 획득
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMinutes(10));

            if (Boolean.FALSE.equals(lockAcquired)) {
                log.info(" 이미 다른 인스턴스에서 처리 중인 Wallet: {}", wallet.getId());
                continue;
            }

            try {
                long amount = wallet.getAutoConvertAmount();
                if (amount <= 0 || wallet.getDepositBalance() < amount) continue;

                wallet.updateBalance(
                    wallet.getDepositBalance() - amount,
                    wallet.getTokenBalance() + amount
                );

                TransactionReceipt receipt = tokkitTokenService.mint(
                    wallet.getWalletAddress(),
                    BigInteger.valueOf(amount)
                );

                logAndSave(
                    wallet,
                    wallet.getUser().getId(),
                    null,
                    TransactionType.AUTO_CONVERT,
                    TransactionStatus.SUCCESS,
                    amount,
                    "정기 자동 예금 → 토큰 전환",
                    "자동 충전",
                    receipt.getTransactionHash()
                );

                log.info("자동 전환 완료: userId={}, amount={}, txHash={}",
                    wallet.getUser().getId(), amount, receipt.getTransactionHash());

            } catch (Exception e) {
                log.error("자동 전환 실패: userId={}", wallet.getUser().getId(), e);
            }
        }
    }
}