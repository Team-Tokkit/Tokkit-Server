package com.example.Tokkit_server.dto.request;

import lombok.Getter;

@Getter
public class DepositToTokenRequest {
    private Long userId;
    private Long amount;
}