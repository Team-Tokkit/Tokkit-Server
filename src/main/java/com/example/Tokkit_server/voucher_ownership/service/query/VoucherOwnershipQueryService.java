package com.example.Tokkit_server.voucher_ownership.service.query;

import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherOwnershipQueryService {

    // 내 바우처 조회하기
    Page<VoucherOwnershipResponse> getMyVouchers(Long userId, Pageable pageable);

    // 내 바우처 상세 조회하기 (상위 5개의 사용처 조회 - default)
    VoucherOwnershipDetailResponse getVoucherDetail(Long voucherOwnershipId, Long userId, Pageable pageable);

    // 내 바우처 상세 조회하기 (사용처 전체 조회)
    Page<StoreResponse> getAllStoresByVoucherId(Long voucherOwnershipId, Long userId, Pageable pageable);

    // 내 바우처 필터링 및 검색하기
    Page<VoucherOwnershipResponse> searchMyVouchers(VoucherOwnershipSearchRequest request, Pageable pageable);
}
