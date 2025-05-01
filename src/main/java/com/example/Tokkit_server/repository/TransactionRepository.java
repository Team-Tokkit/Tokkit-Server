package com.example.Tokkit_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    List<Transaction> findTop10ByWalletIdOrderByCreatedAtDesc(Long walletId);
}
