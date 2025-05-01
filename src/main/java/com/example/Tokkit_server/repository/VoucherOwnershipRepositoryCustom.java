package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.dto.request.VoucherOwnershipSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherOwnershipRepositoryCustom {
    Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable);
}
