package com.example.Tokkit_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<Wallet> findByUser_Id(Long userId);
    Optional<Wallet> findByMerchant_Id(Long merchantId);
}