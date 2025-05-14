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
    private VoucherOwnershipStatus status;
    private String imageUrl;
  
    public static VoucherOwnershipResponse from(VoucherOwnership voucherOwnership, String imageProxyBaseUrl) {
        return VoucherOwnershipResponse.builder()
                .id(voucherOwnership.getId())
                .voucherName(voucherOwnership.getVoucher().getName())
                .voucherContact(voucherOwnership.getVoucher().getContact())
                .remainingAmount(voucherOwnership.getRemainingAmount())
                .isVoucher(voucherOwnership.getStatus() == VoucherOwnershipStatus.AVAILABLE)
                .status(VoucherOwnershipStatus.AVAILABLE)
                .imageUrl(
                        voucherOwnership.getVoucher().getImage() != null ?
                                imageProxyBaseUrl + voucherOwnership.getVoucher().getImage().getImageUrl()
                                : null
                )
                .build();
    }
}
