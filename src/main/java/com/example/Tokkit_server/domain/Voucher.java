package com.example.Tokkit_server.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
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

    // 바우처 상세 설명
    @Column(nullable = false)
    private String detailDescription;

    // 가맹점 (사용처)
    @ManyToMany(mappedBy = "vouchers", fetch = FetchType.LAZY)
    private List<Store> stores = new ArrayList<>();

    // 바우처 환불 정책
    @Column(nullable = false)
    private String refundPolicy;
    
    // 발급처 (문의처)
    private String contact;

    // 가맹점 연관관계 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_store_id", nullable = false)
    private VoucherStore voucherStore;


}