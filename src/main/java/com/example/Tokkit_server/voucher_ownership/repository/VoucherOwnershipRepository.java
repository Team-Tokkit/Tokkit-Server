package com.example.Tokkit_server.voucher_ownership.repository;

import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long>, VoucherOwnershipRepositoryCustom {
    Page<VoucherOwnership> findByWalletUserId(Long userId, Pageable pageable);
    Optional<VoucherOwnership> findByIdAndWalletUserId(Long id, Long userId);
    List<VoucherOwnership> findByStatusAndVoucher_ValidDateBefore(VoucherOwnershipStatus status, LocalDateTime time);

    // 바우처 + 바우처 스토어 + 스토어를 모두 fetch join으로 한 번에 조회
    @Query("""
        SELECT vo
        FROM VoucherOwnership vo
        JOIN FETCH vo.voucher v
        LEFT JOIN FETCH v.voucherStores vs
        LEFT JOIN FETCH vs.store s
        WHERE vo.wallet.user.id = :userId
    """)
    List<VoucherOwnership> findAllWithVoucherAndStoresByUserId(@Param("userId") Long userId);

    // 만료된 바우처만 fetch join으로 조회 (성능 개선용)
    @Query("""
        SELECT vo
        FROM VoucherOwnership vo
        JOIN FETCH vo.voucher v
        WHERE vo.status = :status
        AND v.validDate < :now
    """)
    List<VoucherOwnership> findByStatusAndVoucherValidDateBeforeWithFetchJoin(
        @Param("status") VoucherOwnershipStatus status,
        @Param("now") LocalDateTime now
    );

    @Query("""
			SELECT vo FROM VoucherOwnership vo
			JOIN vo.voucher v
			JOIN v.voucherStores vs
			WHERE vo.wallet.user.id = :userId
			  AND vs.store.id = :storeId
			  AND vo.status = 'AVAILABLE'
			  AND vo.remainingAmount > 0
			  AND v.validDate >= CURRENT_TIMESTAMP
			ORDER BY  vo.remainingAmount DESC,v.validDate ASC
		""")
    Page<VoucherOwnership> findAvailableVouchersByUserAndStore(
        @Param("userId") Long userId,

        @Param("storeId") Long storeId,
        Pageable pageable
    );
}
