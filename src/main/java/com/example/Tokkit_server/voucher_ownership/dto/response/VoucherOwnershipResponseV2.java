package com.example.Tokkit_server.voucher_ownership.dto.response;

import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoucherOwnershipResponseV2 {
    private Long id;
    private String name;
    private String contact;
    private Integer originalPrice;
    private Long remainingAmount;
    private Boolean isVoucher;
    private VoucherOwnershipStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime validDate;
    private String imageUrl;

    public static VoucherOwnershipResponseV2 from(VoucherOwnership voucherOwnership) {
        Voucher voucher = voucherOwnership.getVoucher();

        return VoucherOwnershipResponseV2.builder()
                .id(voucherOwnership.getId())
                .name(voucher.getName())
                .contact(voucher.getContact())
                .originalPrice(voucher.getOriginalPrice())
                .remainingAmount(voucherOwnership.getRemainingAmount())
                .isVoucher(voucherOwnership.getStatus() == VoucherOwnershipStatus.AVAILABLE)
                .status(voucherOwnership.getStatus())
                .validDate(voucher.getValidDate())
                .imageUrl(
                        voucher.getImage() != null
                                ? "http://localhost:8080/api/s3/" + voucher.getImage().getImageUrl()
                                : null
                )
                .build();
    }
}
