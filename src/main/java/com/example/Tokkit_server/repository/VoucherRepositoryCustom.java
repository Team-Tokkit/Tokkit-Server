package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.request.VoucherSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherRepositoryCustom {
    Page<Voucher> searchVoucher(VoucherSearchRequest request, Pageable pageable);
}