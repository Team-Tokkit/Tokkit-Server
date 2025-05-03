package com.example.Tokkit_server.voucher.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import com.example.Tokkit_server.voucher.service.query.VoucherQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Tag(name = "Vouchers", description = "바우처 관련 API")
public class VoucherController {

    private final VoucherQueryService voucherQueryService;

    // 전체 바우처 조회하기 , 필터링 및 검색하기
    @GetMapping
    @Operation(summary = "전체 바우처 조회 및 필터링/검색하기", description = "공지사항 전체 목록을 조회하고 필터링 및 검색할 수 있는 API입니다.")
    public ApiResponse<Page<VoucherResponse>> getVouchers(@ModelAttribute VoucherSearchRequest request, Pageable pageable) {
        Page<VoucherResponse> result = voucherQueryService.searchVouchers(request, pageable);
        return ApiResponse.onSuccess(result);
    }

    // 바우처 상세 조회하기 (사용처 5개 조회)
    @GetMapping("/details/{id}")
    @Operation(summary = "바우처 상세 조회하기 (상위 5개 사용처 조회)", description = "바우처의 상세 정보를 조회하는 API입니다.")
    public ApiResponse<VoucherDetailResponse> getVoucherDetail(@PathVariable Long id) {
        Pageable pageable = PageRequest.of(0, 5);
        VoucherDetailResponse voucherDetail = voucherQueryService.getVoucherDetail(id, pageable);
        return ApiResponse.onSuccess(voucherDetail);
    }

    // 바우처 상세 조회 (사용처 전체 조회)
    @GetMapping("/details/{id}/stores")
    @Operation(summary = "바우처 상세 조회 (사용처 전체 조회)", description = "바우처의 사용처를 전체 조회하는 API입니다.")
    public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long id, Pageable pageable) {
        Page<StoreResponse> stores = voucherQueryService.getAllStoresByVoucherId(id, pageable);
        return ApiResponse.onSuccess(stores);
    }

}
