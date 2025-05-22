package com.example.Tokkit_server;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.repository.VoucherRepository;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.voucher_ownership.scheduler.VoucherExpirationScheduler;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.enums.WalletType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class VoucherExpirationIntegrationTest {

    @Autowired
    private VoucherOwnershipRepository voucherOwnershipRepository;

    @Autowired
    private VoucherExpirationScheduler voucherExpirationScheduler;

    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    @DisplayName("만료된 바우처가 스케줄러 실행 후 expired 상태로 변경된다")
    void 만료된_바우처가_스케줄러_실행후_expired_상태로_변경된다() {
        // given - 사용자 생성
        User user = User.builder()
                .name("테스트유저")
                .email("test@example.com")
                .password("password")
                .simplePassword("1234")
                .roles("USER")
                .build();
        em.persist(user);

        // given - 가맹점 생성
        Merchant merchant = Merchant.builder()
                .email("merchant@example.com")
                .password("password")
                .simplePassword("1111")
                .businessNumber("123-45-67890")
                .roles("MERCHANT")
                .build();
        em.persist(merchant);

        // given - 바우처 생성
        Voucher voucher = Voucher.builder()
                .name("테스트 바우처")
                .description("설명")
                .detailDescription("상세 설명")
                .price(1000)
                .originalPrice(2000)
                .totalCount(10)
                .remainingCount(10)
                .validDate(LocalDateTime.now().minusDays(1))
                .refundPolicy("환불불가")
                .contact("010-1234-5678")
                .merchant(merchant)
                .build();
        em.persist(voucher);

        // given - 지갑 생성
        Wallet wallet = Wallet.builder()
                .user(user)
                .accountNumber("123-456-7890")
                .walletAddress("WALLET-ADDR-001")
                .depositBalance(10000L)
                .tokenBalance(0L)
                .walletType(WalletType.USER)
                .build();
        em.persist(wallet);

        // given - 바우처 소유권 생성
        VoucherOwnership ownership = VoucherOwnership.builder()
                .voucher(voucher)
                .wallet(wallet)
                .remainingAmount(1000L)
                .status(VoucherOwnershipStatus.AVAILABLE)
                .build();
        voucherOwnershipRepository.save(ownership);

        em.flush();
        em.clear();

        // when - 스케줄러 실행
        voucherExpirationScheduler.expireVouchers();

        // then - 상태 확인
        VoucherOwnership updated = voucherOwnershipRepository.findById(ownership.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(VoucherOwnershipStatus.EXPIRED);
    }
}
