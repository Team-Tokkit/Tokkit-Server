package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.dto.response.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface VoucherQueryService {

    // 전체 바우처 조회하기
    Page<VoucherResponse> getAllVouchers(Pageable pageable);

}
