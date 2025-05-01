package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    // 가맹점주 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대표자명
    private String name;

    // 전화번호
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer simplePassword;

    @Column(nullable = false)
    private String businessNumber;

    // 휴면 상태
    private Boolean isDormant;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "merchant")
    private Wallet wallet;

    @OneToOne(mappedBy = "merchant")
    private Store store;
}