package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.response.VoucherResponse;
import com.example.Tokkit_server.service.query.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherQueryService voucherQueryService;

    // 전체 바우처 조회하기
    @GetMapping
    public ApiResponse<Page<VoucherResponse>> getAllVouchers(Pageable pageable) {
        Page<VoucherResponse> vouchers = voucherQueryService.getAllVouchers(pageable);
        return ApiResponse.onSuccess(vouchers);
    }

}
