package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletBalanceResponse {
    private Long depositBalance;
    private Long tokenBalance;
}