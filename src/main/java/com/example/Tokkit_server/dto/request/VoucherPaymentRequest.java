package com.example.Tokkit_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherPaymentRequest {
    private Long userId;
    private Long voucherOwnershipId;  // 내가 가진 바우처 소유 id
    private Long merchantId;           // 결제하려는 가맹점 id
    private Long amount;               // 결제할 금액
    private String simplePassword;
}
