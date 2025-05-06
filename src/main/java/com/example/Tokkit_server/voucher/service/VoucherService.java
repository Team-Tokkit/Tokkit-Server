package com.example.Tokkit_server.voucher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.voucher.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    /**
     * [1] 바우처 목록 조회 - 검색 + 필터 + 정렬 포함
     */
    public Page<VoucherResponse> searchVouchers(VoucherSearchRequest request, Pageable pageable) {
        return voucherRepository.searchVouchers(request, pageable)
            .map(VoucherResponse::from);
    }

    /**
     * [2] 바우처 상세 정보 + 상위 5개 사용처 포함
     */
    public VoucherDetailResponse getVoucherDetail(Long id, Pageable pageable) {
        Voucher voucher = voucherRepository.findById(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        Page<StoreResponse> stores = voucherRepository.findStoresByVoucherId(id, pageable);

        return VoucherDetailResponse.from(voucher, stores);
    }

    /**
     * [3] 바우처 전체 사용처 조회
     */
    public Page<StoreResponse> getAllStoresByVoucherId(Long id, Pageable pageable) {
        return voucherRepository.findStoresByVoucherId(id, pageable);
    }
}
