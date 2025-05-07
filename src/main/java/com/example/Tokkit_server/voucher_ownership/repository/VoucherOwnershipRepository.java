package com.example.Tokkit_server.voucher_ownership.repository;

import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long>, VoucherOwnershipRepositoryCustom {
    Page<VoucherOwnership> findByWalletUserId(Long userId, Pageable pageable);

    Optional<VoucherOwnership> findByIdAndWalletUserId(Long id, Long userId);
}
