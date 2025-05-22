package com.example.Tokkit_server.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MerchantWalletBalanceResponse {
    private Long depositBalance;
    private Long tokenBalance;
    private String storeName;
    private String accountNumber;
}
