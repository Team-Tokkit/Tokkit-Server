package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.dto.response.VoucherOwnershipResponse;
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

    // 내 바우처 조회하기
    @Override
    public Page<VoucherOwnershipResponse> getMyVouchers(Long userId, Pageable pageable) {
        return voucherOwnershipRepository.findByWalletUserId(userId, pageable)
                .map(VoucherOwnershipResponse::from);
    }
}
