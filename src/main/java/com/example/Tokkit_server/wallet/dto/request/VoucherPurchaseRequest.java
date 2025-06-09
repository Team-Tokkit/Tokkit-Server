package com.example.Tokkit_server.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherPurchaseRequest {
    private Long voucherId;
    private String simplePassword; // 간편 비밀번호
}
