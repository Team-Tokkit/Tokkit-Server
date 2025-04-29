package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.dto.response.StoreResponse;
import com.example.Tokkit_server.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.dto.response.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherQueryService {

    // 전체 바우처 조회하기
    Page<VoucherResponse> getAllVouchers(Pageable pageable);

    // 바우처 상세 조회하기 (상위 5개의 사용처 조회 - default)
    VoucherDetailResponse getVoucherDetail(Long id, Pageable pageable);

    // 바우처 상세 조회 (사용처 전체 조회)
    Page<StoreResponse> getAllStoresByVoucherId(Long id, Pageable pageable);
}
