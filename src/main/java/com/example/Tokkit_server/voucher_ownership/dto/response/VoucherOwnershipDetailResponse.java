package com.example.Tokkit_server.voucher_ownership.dto.response;

import java.time.LocalDateTime;

import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import org.springframework.data.domain.Page;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;

import lombok.Builder;
import lombok.Getter;

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
    private VoucherOwnershipStatus status;
    private String imageUrl;

    private Page<StoreResponse> stores;

    public static VoucherOwnershipDetailResponse from(VoucherOwnership ownership, Page<StoreResponse> stores) {
        return VoucherOwnershipDetailResponse.builder()
                .id(ownership.getId())
                .voucherName(ownership.getVoucher().getName())
                .voucherContact(ownership.getVoucher().getContact())
                .voucherValidDate(ownership.getVoucher().getValidDate())
                .voucherDetailDescription(ownership.getVoucher().getDetailDescription())
                .voucherRefundPolicy(ownership.getVoucher().getRefundPolicy())
                .remainingAmount(ownership.getRemainingAmount())
                .status(VoucherOwnershipStatus.AVAILABLE)
                .stores(stores)
                .imageUrl(
                        ownership.getVoucher().getImage() != null ?
                                "http://localhost:8080/api/s3/" + ownership.getVoucher().getImage().getImageUrl()
                                : null
                )
                .build();
    }
}