package com.example.Tokkit_server.voucher_ownership.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherOwnershipService {

    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final StoreRepository storeRepository;

    // 1. 내 바우처 필터/검색/정렬 기반 목록 조회
    public Page<VoucherOwnershipResponse> searchMyVouchers(VoucherOwnershipSearchRequest request, Pageable pageable) {
        return voucherOwnershipRepository.searchMyVoucher(request, pageable)
                .map(VoucherOwnershipResponse::from);
    }

    // 2. 바우처 소유 ID로 상세 조회 (사용처 5개 포함)
    public VoucherOwnershipDetailResponse getVoucherDetail(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        Page<StoreResponse> stores = storeRepository.findByVoucherId(
                voucherOwnership.getVoucher().getId(), pageable);

        return VoucherOwnershipDetailResponse.from(voucherOwnership, stores);
    }

    // 3. 전체 사용처 조회
    public Page<StoreResponse> getAllStoresByVoucherId(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        return storeRepository.findByVoucherId(voucherOwnership.getVoucher().getId(), pageable);
    }
}