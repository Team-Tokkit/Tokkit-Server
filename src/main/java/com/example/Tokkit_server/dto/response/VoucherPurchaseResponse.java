package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherPurchaseResponse {
    private Long ownershipId; // 생성된 VoucherOwnership ID
    private String message;
}
