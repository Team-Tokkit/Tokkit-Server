package com.example.Tokkit_server.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VoucherPaymentResponse {
    private String paymentTime;
    private Long amount;
    private Long remainingAmount;   // 결제 후 남은 바우처 금액
    private String message;         // "결제 성공", "잔액 부족" 등
}
