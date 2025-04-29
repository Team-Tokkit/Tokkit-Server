package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.service.query.VoucherOwnershipQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
