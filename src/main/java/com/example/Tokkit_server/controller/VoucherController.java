package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.voucher.VoucherResponseDto;
import com.example.Tokkit_server.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // 전체 바우처 목록 조회
    @GetMapping
    public ResponseEntity<List<VoucherResponseDto>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.findAll());
    }

    // 바우처 필터링
    @GetMapping("/category")
    public ResponseEntity<List<VoucherResponseDto>> getAllVoucherCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "deadline_desc") String sortBy) {

        Voucher.Category categoryEnum = Voucher.Category.valueOf(category.toUpperCase());
        return ResponseEntity.ok(voucherService.findByCategoryAndSort(categoryEnum, sortBy));
    }

    // 바우처 검색
    @GetMapping("/search")
    public ResponseEntity<List<VoucherResponseDto>> searchVouchers(@RequestParam("search") String keyword) {
        return ResponseEntity.ok(voucherService.searchVouchers(keyword));
    }

    // 바우처 상세 조회
    @GetMapping("/details")
    public ResponseEntity<VoucherResponseDto> voucherDetails(@RequestParam Long id) {
        return ResponseEntity.ok(voucherService.findByDetails(id));
    }

    // 바우처 신청하기
    @PostMapping("/apply/{voucherId}")
    public ResponseEntity<String> applyVoucher(
            @PathVariable Long voucherId,
            @RequestParam Long userId
    ) {
        voucherService.applyVoucher(userId, voucherId);
        return ResponseEntity.ok("바우처 신청 완료");
    }

}
