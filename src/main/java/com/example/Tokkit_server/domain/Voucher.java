package com.example.Tokkit_server.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 바우처 아이디
    private Long id;

    // 바우처 이름
    @Column(nullable = false)
    private String name;

    // 바우처 설명
    @Column(columnDefinition = "TEXT")
    private String description;

    // 바우처 카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public enum Category {
        JOB,
        CULTURE,
        MEDICAL,
        EDUCATION,
        DIGITAL,
        CARE,
    }

    // 바우처 가격
    @Column(nullable = false)
    private Integer price;

    // 지원 금액
    @Column(nullable = false)
    private Integer supportAmount;

    // 총 지원 금액
    @Column(nullable = false)
    private Integer totalSupportAmount;

    // 유효기간
    @Column(nullable = false)
    private LocalDateTime validDate;

    // 발급처 (문의처)
    @Column(nullable = false)
    private String contact;

    // 가맹점 연관관계 (ManyToOne)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "merchant_id", nullable = false)
//    pricate Merchant merchant;
}
