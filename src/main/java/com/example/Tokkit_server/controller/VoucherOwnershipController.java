package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.dto.response.StoreResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.service.query.VoucherOwnershipQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-vouchers")
@RequiredArgsConstructor
@Tag(name = "MyVouchers", description = "내 바우처 관련 API")
public class VoucherOwnershipController {

    private final VoucherOwnershipQueryService voucherOwnershipQueryService;

    @GetMapping
    @Operation(summary = "내 바우처 조회 및 필터링/검색하기", description = "내가 보유한 바우처 목록을 조회하고 필터링 및 검색할 수 API입니다.")
    public ApiResponse<Page<VoucherOwnershipResponse>> getMyVouchers(@ModelAttribute VoucherOwnershipSearchRequest request, Pageable pageable) {
        Page<VoucherOwnershipResponse> myVouchers = voucherOwnershipQueryService.searchMyVouchers(request, pageable);
        return ApiResponse.onSuccess(myVouchers);
    }

    @GetMapping("/details/{voucherOwnershipId}")
    @Operation(summary = "내 바우처 상세 조회하기 (상위 5개 사용처 조회)", description = "내가 보유한 바우처의 상세 정보를 조회하는 API입니다.")
    public ApiResponse<VoucherOwnershipDetailResponse> getVoucherDetails(@PathVariable Long voucherOwnershipId, @RequestParam Long userId) {
        Pageable pageable = PageRequest.of(0, 5);
        VoucherOwnershipDetailResponse voucherDetail = voucherOwnershipQueryService.getVoucherDetail(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(voucherDetail);
    }

    @GetMapping("/details/{voucherOwnershipId}/stores")
    @Operation(summary = "내 바우처 상세 조회하기 (사용처 전체 조회)", description = "내가 보유한 바우처의 사용처를 전체 조회하는 API입니다.")
    public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long voucherOwnershipId, @RequestParam Long userId, Pageable pageable) {
        Page<StoreResponse> stores = voucherOwnershipQueryService.getAllStoresByVoucherId(voucherOwnershipId, userId, pageable);
        return ApiResponse.onSuccess(stores);
    }

}
