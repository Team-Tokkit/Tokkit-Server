package com.example.Tokkit_server.voucher.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.voucher.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import com.example.Tokkit_server.voucher.service.VoucherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vouchers")
@Tag(name = "Vouchers", description = "바우처 관련 API")
public class VoucherController {

	private final VoucherService voucherService;

	// [1] 바우처 전체 목록 + 필터/검색 + 정렬 조회
	@GetMapping
	@Operation(summary = "전체 바우처 조회 및 필터링/검색", description = "카테고리, 키워드, 정렬 조건으로 바우처 목록을 조회합니다.")
	public ApiResponse<Page<VoucherResponse>> getVouchers(@ModelAttribute VoucherSearchRequest request, Pageable pageable) {
		return ApiResponse.onSuccess(voucherService.searchVouchers(request, pageable));
	}

	// [2] 바우처 상세 조회 (사용처 5개 포함)
	@GetMapping("/details/{id}")
	@Operation(summary = "바우처 상세 조회", description = "특정 바우처의 상세 정보를 조회합니다. 사용처는 5개만 포함됩니다.")
	public ApiResponse<VoucherDetailResponse> getVoucherDetail(@PathVariable Long id) {
		Pageable pageable = PageRequest.of(0, 5);
		return ApiResponse.onSuccess(voucherService.getVoucherDetail(id, pageable));
	}

	// [3] 바우처 사용처 전체 조회
	@GetMapping("/details/{id}/stores")
	@Operation(summary = "바우처 사용처 전체 조회", description = "특정 바우처의 사용처 전체 목록을 조회합니다.")
	public ApiResponse<Page<StoreResponse>> getAllStoresByVoucherId(@PathVariable Long id, Pageable pageable) {
		return ApiResponse.onSuccess(voucherService.getAllStoresByVoucherId(id, pageable));
	}
}
