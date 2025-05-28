package com.example.Tokkit_server.voucher_ownership.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;

interface VoucherOwnershipRepositoryCustom {
    Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Long userId, Pageable pageable);
}