package com.example.Tokkit_server.voucher_ownership.scheduler;

import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherExpirationScheduler {

    private final VoucherOwnershipRepository voucherOwnershipRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void expireVouchers() {
        log.info("바우처 유효기간 만료 체크 시작");

        List<VoucherOwnership> expiredList =
                voucherOwnershipRepository.findByStatusAndVoucher_ValidDateBefore(
                        VoucherOwnershipStatus.AVAILABLE, LocalDateTime.now());

        expiredList.forEach(VoucherOwnership::expire);
        voucherOwnershipRepository.saveAll(expiredList);

        log.info("만료 처리된 바우처 수: {}", expiredList.size());

    }
}
