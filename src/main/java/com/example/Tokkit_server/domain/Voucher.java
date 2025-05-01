package com.example.Tokkit_server.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Voucher extends BaseTimeEntity {
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
    private StoreCategory category;

    // 바우처 가격
    @Column(nullable = false)
    private Integer price;

    // 유효기간
    @Column(nullable = false)
    private LocalDateTime validDate;

    // 발급처 (문의처)
    private String contact;


    // 가맹점 연관관계 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "voucher")
    private List<VoucherOwnership> ownerships;

}