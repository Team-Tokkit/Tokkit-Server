package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.VoucherOwnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long>, VoucherOwnershipRepositoryCustom {

    // userId로 바우처 소유권 조회
    Page<VoucherOwnership> findByWalletUserId(Long userId, Pageable pageable);

    // userId, voucherOwnershipId로 바우처 소유권 조회
    Optional<VoucherOwnership> findByIdAndWalletUserId(Long voucherOwnershipId, Long userId);

}
