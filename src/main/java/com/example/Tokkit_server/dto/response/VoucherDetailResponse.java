package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.Voucher;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoucherDetailResponse {
    private Long id;
    private String name;
    private Integer price;
    private LocalDateTime validDate;
    private String detailDescription;
    private String refundPolicy;
    private String contact;

    private Page<StoreResponse> stores;

    public static VoucherDetailResponse from (Voucher voucher, Page<StoreResponse> stores) {
        return VoucherDetailResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .price(voucher.getPrice())
                .validDate(voucher.getValidDate())
                .detailDescription(voucher.getDetailDescription())
                .refundPolicy(voucher.getRefundPolicy())
                .contact(voucher.getContact())
                .stores(stores)
                .build();
    }
}
