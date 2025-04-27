package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.domain.VoucherOwnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long> {

    // 사용자별 바우처 검색 및 필터링
    Page<VoucherOwnership> findByUserAndVoucher_NameContainingIgnoreCaseOrUserAndVoucher_DescriptionContainingIgnoreCase(
            User user1, String nameKeyword, User user2, String descriptionKeyword, Pageable pageable
    );

    // 바우처 소유권 ID로 조회
    Optional<VoucherOwnership> findByIdAndUserId(Long id, Long userId);


}
