package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.voucher.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>, VoucherCustomRepository {

    // Merchant ID로 바우처 목록 조회
    @EntityGraph(attributePaths = "image")
    Page<Voucher> findAllByMerchantId(Long merchantId, Pageable pageable);

    // 바우처 이름으로 검색
    Page<Voucher> findByMerchantIdAndNameContaining(Long merchantId, String Keyword, Pageable pageable);
}
