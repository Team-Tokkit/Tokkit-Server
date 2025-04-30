package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.response.StoreResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.service.query.VoucherOwnershipQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-vouchers")
@RequiredArgsConstructor
public class VoucherOwnershipController {

    private final VoucherOwnershipQueryService voucherOwnershipQueryService;

    // 내 바우처 조회하기
    @GetMapping
    public ApiResponse<Page<VoucherOwnershipResponse>> getMyVouchers(@RequestParam Long userId, Pageable pageable) {
        Page<VoucherOwnershipResponse> myVouchers = voucherOwnershipQueryService.getMyVouchers(userId, pageable);
        return ApiResponse.onSuccess(myVouchers);
    }

    // 내 바우처 상세 조회하기 - 상위 5개의 사용처 조회 (default)
    @GetMapping("/details/{voucherOwnershipId}")
    public ApiResponse<VoucherOwnershipDetailResponse> getVoucherDetails(@PathVariable Long voucherOwnershipId, @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(0, 5);
        VoucherOwnershipDetailResponse voucherDetail = voucherOwnershipQueryService.getVoucherDetail(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(voucherDetail);
    }

    // 내 바우처 상세 조회하기 - 사용처 전체 조회
    @GetMapping("/details/{voucherOwnershipId}/stores")
    public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long voucherOwnershipId, @RequestParam Long userId, Pageable pageable) {
        Page<StoreResponse> stores = voucherOwnershipQueryService.getAllStoresByVoucherId(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(stores);
    }

}
