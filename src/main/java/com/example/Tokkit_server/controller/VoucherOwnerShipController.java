package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.dto.voucher.VoucherOwnershipResponseDto;
import com.example.Tokkit_server.service.VoucherOwnershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-vouchers")
@RequiredArgsConstructor
public class VoucherOwnerShipController {

    private final VoucherOwnershipService voucherOwnershipService;

    // 내 바우처 목록 조회 (검색 + 정렬)
    @GetMapping
    public ResponseEntity<Page<VoucherOwnershipResponseDto>> getMyVouchers(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "acquiredAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<VoucherOwnershipResponseDto> myVouchers = voucherOwnershipService.getMyVouchers(userId, keyword, sortBy, sortOrder, page, size);
        return ResponseEntity.ok(myVouchers);
    }

    // 내 바우처 상세 조회
    @GetMapping("/details")
    public ResponseEntity<VoucherOwnershipResponseDto> getVoucherDetail(
            @RequestParam Long userId,
            @RequestParam Long voucherOwnershipId) {

        VoucherOwnershipResponseDto detail = voucherOwnershipService.findMyVoucherDetail(userId, voucherOwnershipId);
        return ResponseEntity.ok(detail);
    }

    // 바우처 신청 철회
    @DeleteMapping("/delete")
    public ResponseEntity<String> cancelVoucher(
            @RequestParam Long userId,
            @RequestParam Long voucherOwnershipId) {
        voucherOwnershipService.cancelVoucher(userId, voucherOwnershipId);
        return ResponseEntity.ok("바우처 신청이 철회되었습니다.");
    }
}
