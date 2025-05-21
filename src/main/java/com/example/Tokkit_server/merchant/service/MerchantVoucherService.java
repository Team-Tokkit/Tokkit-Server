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
     * [1] Merchant의 전체 바우처 목록 조회
     */
    public Page<VoucherResponse> getAllVouchers(Long merchantId, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.findAllByMerchantId(merchantId, pageable);
        return vouchers.map(voucher -> VoucherResponse.from(voucher, imageProxyBaseUrl));
    }

}
