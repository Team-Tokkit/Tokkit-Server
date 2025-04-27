package com.example.Tokkit_server.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VoucherOwnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 바우처 소유권 ID
    private Long id;

    // 바우처 잔액
    @Column(nullable = false)
    private Long field;

    // 바우처 상태
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        WAITING,    // 발급 대기중
        AVAILABLE,  // 사용 가능
        EXPIRED     // 만료됨
    }

    // 결제횟수
    @Column(nullable = false)
    private Integer account;

    // 소유권 생성 시점 (소유한 날짜 및 시간)
    @Column(nullable = false)
    private LocalDateTime acquiredAt;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 소유한 바우처
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    // 가맹점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
