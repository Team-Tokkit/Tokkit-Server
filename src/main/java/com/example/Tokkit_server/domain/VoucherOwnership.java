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

    private Long userId; // 구매한 유저 id

    private Long voucherId; // 연결된 바우처 id (Voucher 엔티티를 나중에 추가 가능)

    private Long remainingAmount; // 남은 바우처 금액

    private Boolean isUsed; // 전액 소진 여부 (optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public void useAmount(Long amount) {
        if (this.remainingAmount < amount) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        this.remainingAmount -= amount;
    }
}
