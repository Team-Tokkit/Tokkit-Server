package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherRepositoryCustom {
    Page<Voucher> searchVoucher(VoucherSearchRequest request, Pageable pageable);
}
