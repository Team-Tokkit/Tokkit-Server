package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.VoucherOwnership;
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

    public static VoucherOwnershipResponse from(VoucherOwnership voucherOwnership) {
        return VoucherOwnershipResponse.builder()
                .id(voucherOwnership.getId())
                .voucherName(voucherOwnership.getVoucher().getName())
                .voucherContact(voucherOwnership.getVoucher().getContact())
                .remainingAmount(voucherOwnership.getRemainingAmount())
                .isVoucher(voucherOwnership.getIsUsed())
                .build();
    }
}
