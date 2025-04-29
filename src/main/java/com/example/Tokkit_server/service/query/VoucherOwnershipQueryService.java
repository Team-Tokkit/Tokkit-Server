package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherOwnershipQueryService {

    // 내 바우처 조회하기
    Page<VoucherOwnershipResponse> getMyVouchers(Long userId, Pageable pageable);

    // 내 바우처 상세 조회하기

    // 내 바우처 필터링 및 검색하기
}
