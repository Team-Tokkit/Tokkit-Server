package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;
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

    private Long remainingAmount; // 남은 바우처 금액

    private Boolean isUsed; // 전액 소진 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    // 남은 금액 차감 메서드
    public void useAmount(Long amount) {
        if (this.remainingAmount < amount) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        this.remainingAmount -= amount;
    }
}