package com.example.Tokkit_server.voucher.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherDetailResponse;
import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.voucher.repository.VoucherJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherQueryServiceImpl implements VoucherQueryService {

    private final VoucherJpaRepository voucherRepository;
    private final StoreRepository storeRepository;

    // 전체 바우처 조회하기
    @Override
    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAll(pageable)
                .map(VoucherResponse::from);
    }

    // 바우처 상세 조회 (상위 5개의 사용처 조회)
    @Override
    public VoucherDetailResponse getVoucherDetail(Long id, Pageable pageable) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOUCHER_NOT_FOUND));

        Page<StoreResponse> stores = storeRepository.findByVouchersId(id, pageable)
                .map(StoreResponse::from);

        return VoucherDetailResponse.from(voucher, stores);
    }

    // 바우처 상세 조회 (사용처 전체 조회)
    @Override
    public Page<StoreResponse> getAllStoresByVoucherId(Long id, Pageable pageable) {
        return storeRepository.findByVouchersId(id, pageable)
                .map(StoreResponse::from);
    }

    // 바우처 필터링 및 검색
    @Override
    public Page<VoucherResponse> searchVouchers(VoucherSearchRequest request, Pageable pageable) {
        return voucherRepository.searchVoucher(request,pageable)
                .map(VoucherResponse::from);
    }

}
