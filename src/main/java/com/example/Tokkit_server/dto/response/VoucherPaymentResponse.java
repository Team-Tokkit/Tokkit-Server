package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherPaymentResponse {
    private Long remainingAmount;   // 결제 후 남은 바우처 금액
    private String message;         // "결제 성공", "잔액 부족" 등
}
