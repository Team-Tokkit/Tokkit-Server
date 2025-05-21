package com.example.Tokkit_server.merchant.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.merchant.service.MerchantVoucherService;
import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Tag(name = "Merchant", description = "가맹점 바우처 관련 API")
public class MerchantVoucherController {

    private final MerchantVoucherService merchantVoucherService;

    @GetMapping("/vouchers")
    @Operation(summary = "가맹점주의 전체 바우처 목록 조회 및 검색", description = "가맹점주가 소유한 전체 바우처 목록을 조회하고 검색하는 API입니다.")
    public ApiResponse<Page<VoucherResponse>> getAllVouchers(
            @AuthenticationPrincipal CustomMerchantDetails merchantDetails,
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable)
    {
        Page<VoucherResponse> vouchers = merchantVoucherService.getAllVouchers(merchantDetails.getId(), keyword, pageable);
        return ApiResponse.onSuccess(vouchers);
    }
}
