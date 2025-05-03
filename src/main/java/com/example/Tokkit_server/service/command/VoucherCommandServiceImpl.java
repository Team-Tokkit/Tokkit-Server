package com.example.Tokkit_server.service.command;

import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherCommandServiceImpl implements VoucherCommandService {

    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @Transactional
    @Override
    public void purchaseVoucher(Long voucherId, Long userId) {
        // 바우처 구매 로직 구현
    }

    @Transactional
    @Override
    public void refundVoucher(Long voucherId, Long userId) {
        // 바우처 환불 로직 구현
    }

}
