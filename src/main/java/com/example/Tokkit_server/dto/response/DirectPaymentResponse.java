package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectPaymentResponse {
    private Long remainingTokenBalance;
    private String message;
}
