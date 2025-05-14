package com.example.Tokkit_server.voucher_ownership.dto.response;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.enums.VoucherOwnershipStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoucherOwnershipDetailResponseV2 {
    private Long id;
    private String voucherName;
    private String voucherContact;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime voucherValidDate;
    private String voucherDetailDescription;
    private String voucherRefundPolicy;
    private Integer originalPrice;
    private Long remainingAmount;
    private VoucherOwnershipStatus status;
    private String imageUrl;

    private Page<StoreResponse> stores;

    public static VoucherOwnershipDetailResponseV2 from(VoucherOwnership ownership, Page<StoreResponse> stores) {

        return VoucherOwnershipDetailResponseV2.builder()
                .id(ownership.getId())
                .voucherName(ownership.getVoucher().getName())
                .voucherContact(ownership.getVoucher().getContact())
                .voucherValidDate(ownership.getVoucher().getValidDate())
                .voucherDetailDescription(ownership.getVoucher().getDetailDescription())
                .voucherRefundPolicy(ownership.getVoucher().getRefundPolicy())
                .originalPrice(ownership.getVoucher().getOriginalPrice())
                .remainingAmount(ownership.getRemainingAmount())
                .status(ownership.getStatus())
                .stores(stores)
                .imageUrl(
                        ownership.getVoucher().getImage() != null ?
                                "http://localhost:8080/api/s3/" + ownership.getVoucher().getImage().getImageUrl()
                                : null
                )
                .build();
    }
}