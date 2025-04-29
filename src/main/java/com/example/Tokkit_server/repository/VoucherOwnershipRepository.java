package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.VoucherOwnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long> {

    // 특정 유저가 소유한 바우처 조회
    Page<VoucherOwnership> findByWalletUserId(Long userId, Pageable pageable);
}
