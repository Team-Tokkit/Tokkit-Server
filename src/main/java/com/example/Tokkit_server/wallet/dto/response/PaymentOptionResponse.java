package com.example.Tokkit_server.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOptionResponse {
    private String type; // "TOKEN" or "VOUCHER"
    private Long voucherOwnershipId; // 바우처일 경우만 존재
    private String name; // 바우처명 또는 토큰으로 결제
    private Long balance;
    private String expireDate; // yyyy-MM-dd
    private boolean usable;
    private String storeCategory; // 카테고리 (FOOD, SERVICE...)
}