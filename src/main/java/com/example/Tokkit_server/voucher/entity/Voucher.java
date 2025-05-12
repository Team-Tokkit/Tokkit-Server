package com.example.Tokkit_server.voucher.entity;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


import com.example.Tokkit_server.global.entity.VoucherImage;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.enums.StoreCategory;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.global.entity.VoucherStore;
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

    // 할인금액
    @Column(nullable = false)
    private Integer price;

    // 정가
    @Column(nullable = false)
    private Integer originalPrice;

    // 총 바우처 발행 개수
    @Column(nullable = false)
    private Integer totalCount;

    // 남은 개수
    @Column(nullable = false)
    private Integer remainingCount;

    @Column(nullable = false)
    private LocalDateTime validDate;

    @Column(nullable = false)
    private String refundPolicy;

    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Builder.Default
    @OneToMany(mappedBy = "voucher")
    private List<VoucherOwnership> ownerships = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private List<VoucherStore> voucherStores = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StoreCategory storeCategory;

    @OneToOne(mappedBy = "voucher", cascade = CascadeType.ALL)
    private VoucherImage image;

    public void addVoucherStore(Store store) {
        VoucherStore vs = VoucherStore.builder()
                .voucher(this)
                .store(store)
                .build();
        this.voucherStores.add(vs);
    }

    public void decreaseRemainingCount() {
        if (this.remainingCount <= 0) {
            throw new IllegalStateException("잔여 수량이 부족합니다.");
        }
        this.remainingCount -= 1;
    }

}