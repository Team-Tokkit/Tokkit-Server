package com.example.Tokkit_server.domain;

import lombok.Getter;

@Getter
public class PaymentRequest {
	private Long userId;
	private Long voucherOwnershipId; //  사용하려는 바우처 소유 ID
	private Long amount;
}
