package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.VoucherOwnership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherOwnershipRepository extends JpaRepository<VoucherOwnership, Long> {
    List<VoucherOwnership> findByUserId(Long userId);
}
