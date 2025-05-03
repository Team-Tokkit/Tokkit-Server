package com.example.Tokkit_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectPaymentRequest {
    private Long userId;
    private String simplePassword;
    private Long merchantId;
    private Long amount;
}
