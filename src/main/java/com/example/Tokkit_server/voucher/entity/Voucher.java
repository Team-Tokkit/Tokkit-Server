package com.example.Tokkit_server.voucher.entity;

import java.time.LocalDateTime;

import java.util.List;


import com.example.Tokkit_server.store_category.entity.StoreCategory;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.voucher_stroe.entity.VoucherStore;
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
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String detailDescription;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalDateTime validDate;

    @Column(nullable = false)
    private String refundPolicy;

    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "voucher")
    private List<VoucherOwnership> ownerships;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_store_id", nullable = false)
    private VoucherStore voucherStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private StoreCategory category;
}