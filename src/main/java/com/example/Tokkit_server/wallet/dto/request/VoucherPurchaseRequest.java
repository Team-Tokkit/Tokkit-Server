package com.example.Tokkit_server.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherPurchaseRequest {
    private Long userId;
    private Long voucherId; // 구매하려는 바우처 ID
    private String simplePassword; // 간편 비밀번호
}
