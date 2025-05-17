package com.example.Tokkit_server.transaction.service.query;

import com.example.Tokkit_server.transaction.entity.Transaction;
import com.example.Tokkit_server.transaction.enums.TransactionType;
import com.example.Tokkit_server.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionRepository transactionRepository;

    public void logAndSave(Transaction transaction, Long userId, Long merchantId) {
        String traceId = transaction.getTraceId();
        String target = (transaction.getType() == TransactionType.RECEIVE) ? "MERCHANT" : "USER";

        log.info("[TXN][{}] traceId={}, userId={}, merchantId={}, type={}, status={}, amount={}, desc={}",
                target,
                traceId,
                userId != null ? userId : "-",
                merchantId != null ? merchantId : "-",
                transaction.getType(),
                transaction.getStatus(),
                transaction.getAmount(),
                transaction.getDescription());

        transactionRepository.save(transaction);
    }
}