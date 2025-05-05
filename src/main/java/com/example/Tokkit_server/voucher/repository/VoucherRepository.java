package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>, VoucherRepositoryCustom {

    @Query("SELECT new com.example.Tokkit_server.store.dto.response.StoreResponse(s) " +
            "FROM VoucherStore vs JOIN vs.store s " +
            "WHERE vs.voucher.id = :voucherId")
    Page<StoreResponse> findStoresByVoucherId(@Param("voucherId") Long voucherId, Pageable pageable);
}
