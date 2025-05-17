package com.example.Tokkit_server.wallet.dto.request;

import lombok.Getter;

@Getter
public class TokenToDepositRequest {
    private Long userId;
    private Long amount;
    private String simplePassword;
}