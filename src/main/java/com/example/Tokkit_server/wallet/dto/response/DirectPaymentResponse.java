package com.example.Tokkit_server.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectPaymentResponse {
    private String paymentTime;
    private Long amount;
    private Long remainingTokenBalance;
    private String message;
}
