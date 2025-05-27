package com.example.Tokkit_server.transaction.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    List<Transaction> findTop10ByWalletIdOrderByCreatedAtDesc(Long walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId " +
            "AND t.type = com.example.Tokkit_server.transaction.enums.TransactionType.RECEIVE " +
            "AND t.createdAt BETWEEN :startOfDay AND :endOfDay")
    Long findTodayRevenueByWalletId(@Param("walletId") Long walletId,
                                    @Param("startOfDay") LocalDateTime startOfDay,
                                    @Param("endOfDay") LocalDateTime endOfDay);
}
