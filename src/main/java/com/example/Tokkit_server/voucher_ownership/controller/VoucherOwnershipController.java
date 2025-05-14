package com.example.Tokkit_server.voucher_ownership.controller;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipDetailResponseV2;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipResponseV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.voucher_ownership.service.VoucherOwnershipService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/my-vouchers")
@RequiredArgsConstructor
@Tag(name = "MyVouchers", description = "내 바우처 관련 API")
public class VoucherOwnershipController {

    private final VoucherOwnershipService voucherOwnershipService;

    // 내 바우처 전체 조회하기, 필터링 및 검색하기
    @GetMapping
    @Operation(summary = "내 바우처 조회 및 필터링/검색하기", description = "내가 보유한 바우처 목록을 조회하고 필터링 및 검색할 수 API입니다.")
    public ApiResponse<Page<VoucherOwnershipResponseV2>> getMyVouchers(@ModelAttribute VoucherOwnershipSearchRequest request, Pageable pageable) {
        Page<VoucherOwnershipResponseV2> myVouchers = voucherOwnershipService.searchMyVouchers(request, pageable);
        return ApiResponse.onSuccess(myVouchers);
    }

    // 내 바우처 상세 조회하기 - 상위 5개의 사용처 조회 (default)
    @GetMapping("/details/{voucherOwnershipId}")
    public ApiResponse<VoucherOwnershipDetailResponseV2> getVoucherDetails(
            @PathVariable Long voucherOwnershipId, @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(0, 5);
        VoucherOwnershipDetailResponseV2 voucherDetail = voucherOwnershipService.getVoucherDetail(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(voucherDetail);
    }

    // 내 바우처 상세 조회하기 - 사용처 전체 조회
    @GetMapping("/details/{voucherOwnershipId}/stores")
    @Operation(summary = "내 바우처 상세 조회하기 (사용처 전체 조회)", description = "내가 보유한 바우처의 사용처를 전체 조회하는 API입니다.")
    public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long voucherOwnershipId, @RequestParam Long userId, Pageable pageable) {
        Page<StoreResponse> stores = voucherOwnershipService.getAllStoresByVoucherId(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(stores);
    }
} 
