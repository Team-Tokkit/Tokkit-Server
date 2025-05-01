package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.VoucherOwnership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long> {
    @Query("SELECT vo FROM VoucherOwnership vo WHERE vo.wallet.user.id = :userId")
    List<VoucherOwnership> findByUserId(@Param("userId") Long userId);

}
