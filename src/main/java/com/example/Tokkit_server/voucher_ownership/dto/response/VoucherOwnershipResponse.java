package com.example.Tokkit_server.voucher_ownership.dto.response;

import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoucherOwnershipResponse {
    private Long id;
    private String voucherName;
    private String voucherContact;
    private Long remainingAmount;
    private Boolean isVoucher;
    private String status;

    public static VoucherOwnershipResponse from(VoucherOwnership voucherOwnership) {
        return VoucherOwnershipResponse.builder()
                .id(voucherOwnership.getId())
                .voucherName(voucherOwnership.getVoucher().getName())
                .voucherContact(voucherOwnership.getVoucher().getContact())
                .remainingAmount(voucherOwnership.getRemainingAmount())
                .isVoucher(voucherOwnership.getStatus() == VoucherOwnershipStatus.AVAILABLE)
                .status(voucherOwnership.getStatus().name())
                .build();
    }
}
