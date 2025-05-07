package com.example.Tokkit_server.voucher.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.voucher.entity.Voucher;

public interface VoucherCustomRepository {
    Page<Voucher> searchVouchers(VoucherSearchRequest request, Pageable pageable);
    Page<StoreResponse> findStoresByVoucherId(Long voucherId, Pageable pageable);
}