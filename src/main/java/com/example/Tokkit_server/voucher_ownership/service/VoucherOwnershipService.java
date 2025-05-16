package com.example.Tokkit_server.voucher_ownership.service;

import com.example.Tokkit_server.global.config.S3Config;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipDetailResponseV2;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipResponseV2;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherOwnershipService {

    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final StoreRepository storeRepository;
    private final String imageProxyBaseUrl;

    /**
     * [1] 내 바우처 필터/검색/정렬 기반 목록 조회
     */
    public Page<VoucherOwnershipResponseV2> searchMyVouchers(VoucherOwnershipSearchRequest request, Pageable pageable) {
        return voucherOwnershipRepository.searchMyVoucher(request, pageable)
                .map(voucherOwnership -> VoucherOwnershipResponseV2.from(voucherOwnership, imageProxyBaseUrl)); // 람다 사용
    }

    /**
     * [2] 바우처 소유 ID로 상세 조회 (사용처 5개 포함)
     */
    public VoucherOwnershipDetailResponseV2 getVoucherDetail(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        Page<StoreResponse> stores = storeRepository.findByVoucherId(
                voucherOwnership.getVoucher().getId(), pageable);

        return VoucherOwnershipDetailResponseV2.from(voucherOwnership, stores, imageProxyBaseUrl); // s3Url 전달
    }

    /**
     * [3] 전체 사용처 조회
     */
    public Page<StoreResponse> getAllStoresByVoucherId(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        return storeRepository.findByVoucherId(voucherOwnership.getVoucher().getId(), pageable);
    }

    /**
     * [4] 바우처 삭제 - soft delete
     */
    public void deleteVoucherOwnership(Long voucherOwnershipId, Long userId) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        if (voucherOwnership.getStatus() == VoucherOwnershipStatus.DELETED) {
            throw new GeneralException(ErrorStatus.VOUCHER_ALREADY_DELETED);
        }

        voucherOwnership.delete();
        voucherOwnershipRepository.save(voucherOwnership);
    }

}