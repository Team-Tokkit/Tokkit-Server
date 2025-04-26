package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.voucher.VoucherResponseDto;
import com.example.Tokkit_server.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // 전체 바우처 목록 조회하기
    @GetMapping
    public ResponseEntity<List<VoucherResponseDto>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.findAll());
    }

    // 바우처 상세 조회하기
    @GetMapping("/details")
    public ResponseEntity<List<Voucher>> VoucherDetails(@RequestParam Long id) {
        return ResponseEntity.ok(voucherService.VoucherDetails(id));
    }


    // 바우처 카테고리 별 필터링
    @GetMapping("/category")
    public ResponseEntity<List<VoucherResponseDto>> getAllVoucherCategory(@RequestParam String category) {
        Voucher.Category categoryEnum = Voucher.Category.valueOf(category.toUpperCase());
        return ResponseEntity.ok(voucherService.findByCategory(categoryEnum));
    }

    // 바우처 검색
    @GetMapping("/search")
    public ResponseEntity<List<VoucherResponseDto>> searchVouchers(@RequestParam("search") String keyword) {
        return ResponseEntity.ok(voucherService.searchVouchers(keyword));
    }

    // 바우처 신청하기


    // 내가 보유중인 바우처 목록 조회


     // 내가 보유중인 바우처 상세 조회


}
