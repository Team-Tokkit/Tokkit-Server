package com.example.Tokkit_server.wallet.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequest {
	private Long voucherOwnershipId;
	private Long amount;
}
