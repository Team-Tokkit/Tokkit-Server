package com.example.Tokkit_server.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.wallet.entity.Wallet;

import javax.swing.text.html.Option;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser_Id(Long userId);

    Optional<Wallet> findByMerchant_Id(Long merchantId);

    boolean existsByUser_Id(Long userId);

    List<Wallet> findByAutoConvertEnabledTrue();
    boolean existsByMerchant_Id(Long merchantId);
}