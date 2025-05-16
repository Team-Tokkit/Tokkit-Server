package com.example.Tokkit_server.voucher_ownership.entity;

import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import com.example.Tokkit_server.voucher.entity.Voucher;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoucherOwnership extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;


    private Long remainingAmount; // 남은 바우처 금액


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherOwnershipStatus status = VoucherOwnershipStatus.AVAILABLE;

    // 남은 금액 차감 메서드
    public void useAmount(Long amount) {
        if (this.status != VoucherOwnershipStatus.AVAILABLE) {
            throw new GeneralException(ErrorStatus.VOUCHER_ALREADY_USED);
        }

        if (this.remainingAmount < amount) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_BALANCE);
        }

        this.remainingAmount -= amount;

        if (this.remainingAmount == 0) {
            this.status = VoucherOwnershipStatus.USED;
        }
    }

    // 바우처 삭제 메서드
    public void delete() {
        this.status = VoucherOwnershipStatus.DELETED;
    }
}