package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.voucher.dto.response.VoucherResponse;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantVoucherService {

    private final VoucherRepository voucherRepository;
    private final String imageProxyBaseUrl;

    /**
     * [1] Merchant의 전체 바우처 목록 조회 및 검색
     */
    public Page<VoucherResponse> getAllVouchers(Long merchantId, String keyword, Pageable pageable) {
        Page<Voucher> vouchers;

        // 바우처 이름으로 검색
        if (keyword == null || keyword.trim().isEmpty()) {
            vouchers = voucherRepository.findAllByMerchantId(merchantId, pageable);
        } else {
            vouchers = voucherRepository.findByMerchantIdAndNameContaining(merchantId, keyword, pageable);
        }

        return vouchers.map(voucher -> VoucherResponse.from(voucher, imageProxyBaseUrl));
    }

}
