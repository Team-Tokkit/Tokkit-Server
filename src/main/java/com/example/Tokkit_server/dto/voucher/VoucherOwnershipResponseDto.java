package com.example.Tokkit_server.dto.voucher;

import com.example.Tokkit_server.domain.VoucherOwnership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherOwnershipResponseDto {

    private Long voucherOwnershipId;
    private String voucherName;
    private String status;
    private Long field;
    private Integer account;
    private Integer totalSupportAmount;
    private String contact;
    private String validDate;

    public static VoucherOwnershipResponseDto from(VoucherOwnership ownership) {
        return VoucherOwnershipResponseDto.builder()
                .voucherOwnershipId(ownership.getId())
                .voucherName(ownership.getVoucher().getName())
                .status(ownership.getStatus().name())
                .field(ownership.getField())
                .account(ownership.getAccount())
                .totalSupportAmount(ownership.getVoucher().getTotalSupportAmount())
                .contact(ownership.getVoucher().getContact())
                .validDate(ownership.getVoucher().getValidDate().toString())
                .build();
    }
}
