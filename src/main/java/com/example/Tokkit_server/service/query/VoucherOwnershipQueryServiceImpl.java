package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.dto.response.StoreResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipDetailResponse;
import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
import com.example.Tokkit_server.repository.StoreRepository;
import com.example.Tokkit_server.repository.VoucherOwnershipRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherOwnershipQueryServiceImpl implements VoucherOwnershipQueryService {

    private final VoucherOwnershipRepository voucherOwnershipRepository;
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
        return VoucherOwnershipRepository.searchMyVouchers(request, pageable)
                .map(VoucherOwnershipResponse::from);
    }

}
