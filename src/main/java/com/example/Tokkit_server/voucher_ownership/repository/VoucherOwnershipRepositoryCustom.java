package com.example.Tokkit_server.voucher_ownership.repository;

import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherOwnershipRepositoryCustom {
    Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable);
}
