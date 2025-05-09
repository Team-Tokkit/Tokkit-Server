package com.example.Tokkit_server.merchant.entity;

import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class Merchant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String simplePassword;

    @Column(nullable = false)
    private String businessNumber;

    private Boolean isDormant;

    private String roles;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "merchant")
    private Wallet wallet;

    @OneToOne(mappedBy = "merchant")
    private Store store;

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}