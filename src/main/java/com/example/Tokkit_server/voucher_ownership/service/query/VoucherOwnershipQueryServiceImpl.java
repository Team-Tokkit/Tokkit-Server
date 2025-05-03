package com.example.Tokkit_server.voucher_ownership.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.voucher_ownership.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherOwnershipQueryServiceImpl implements VoucherOwnershipQueryService {

    private final VoucherOwnershipJpaRepository voucherOwnershipRepository;
    private final StoreRepository storeRepository;

    // 내 바우처 조회하기
    @Override
    public Page<VoucherOwnershipResponse> getMyVouchers(Long userId, Pageable pageable) {
        return voucherOwnershipRepository.findByWalletUserId(userId, pageable)
                .map(VoucherOwnershipResponse::from);
    }

    // 내 바우처 상세 조회 (상위 5개의 사용처 조회 - default)
    @Override
    public VoucherOwnershipDetailResponse getVoucherDetail(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository.findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND)); // userId까지 검증 완료된 상태!

        Page<StoreResponse> stores = storeRepository.findByVouchersId(voucherOwnership.getVoucher().getId(), pageable)
                .map(StoreResponse::from);

        return VoucherOwnershipDetailResponse.from(voucherOwnership, stores);
    }


    // 내 바우처 상세 조회 (사용처 전체 조회)
    @Override
    public Page<StoreResponse> getAllStoresByVoucherId(Long voucherOwnershipId, Long userId, Pageable pageable) {
        VoucherOwnership voucherOwnership = voucherOwnershipRepository
                .findByIdAndWalletUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        return storeRepository.findByVouchersId(voucherOwnership.getVoucher().getId(), pageable)
                .map(StoreResponse::from);
    }

    // 내 바우처 필터링 및 검색하기
    @Override
    public Page<VoucherOwnershipResponse> searchMyVouchers(VoucherOwnershipSearchRequest request, Pageable pageable) {
        return voucherOwnershipRepository.searchMyVoucher(request, pageable)
                .map(VoucherOwnershipResponse::from);
    }

}
