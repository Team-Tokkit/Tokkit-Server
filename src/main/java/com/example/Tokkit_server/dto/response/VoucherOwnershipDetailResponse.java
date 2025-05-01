package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.VoucherOwnership;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoucherOwnershipDetailResponse {
    private Long id;
    private String voucherName;
    private String voucherContact;
    private LocalDateTime voucherValidDate;
    private String voucherDetailDescription;
    private String voucherRefundPolicy;
    private Long remainingAmount;

    private Page<StoreResponse> stores;

    public static VoucherOwnershipDetailResponse from (VoucherOwnership voucherOwnership, Page<StoreResponse> stores) {
        return VoucherOwnershipDetailResponse.builder()
                .id(voucherOwnership.getId())
                .voucherName(voucherOwnership.getVoucher().getName())
                .voucherContact(voucherOwnership.getVoucher().getContact())
                .voucherValidDate(voucherOwnership.getVoucher().getValidDate())
                .voucherDetailDescription(voucherOwnership.getVoucher().getDetailDescription())
                .voucherRefundPolicy(voucherOwnership.getVoucher().getRefundPolicy())
                .remainingAmount(voucherOwnership.getRemainingAmount())
                .stores(stores)
                .build();
    }
}
