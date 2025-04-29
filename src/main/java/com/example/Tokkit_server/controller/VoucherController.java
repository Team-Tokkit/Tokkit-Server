package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.response.StoreResponse;
import com.example.Tokkit_server.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.dto.response.VoucherResponse;
import com.example.Tokkit_server.service.query.VoucherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // 바우처 상세 조회하기
    @GetMapping("/details/{id}")
    public ApiResponse<VoucherDetailResponse> getVoucherDetail(@PathVariable Long id) {
        Pageable pageable = PageRequest.of(0, 5);
        VoucherDetailResponse voucherDetail = voucherQueryService.getVoucherDetail(id, pageable);
        return ApiResponse.onSuccess(voucherDetail);
    }

    // 바우처 상세 조회 (사용처 전체 조회)
    @GetMapping("/details/{id}/stores")
    public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long id, Pageable pageable) {
        Page<StoreResponse> stores = voucherQueryService.getAllStoresByVoucherId(id, pageable);
        return ApiResponse.onSuccess(stores);
    }


}
