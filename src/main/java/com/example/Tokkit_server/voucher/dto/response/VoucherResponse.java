package com.example.Tokkit_server.voucher.dto.response;

import com.example.Tokkit_server.voucher.entity.Voucher;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VoucherResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer originalPrice;
    private LocalDateTime validDate;
    private String contact;
    private Integer remainingCount;


    public static VoucherResponse from (Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .originalPrice(voucher.getOriginalPrice())
                .validDate(voucher.getValidDate())
                .contact(voucher.getContact())
                .remainingCount(voucher.getRemainingCount())
                .build();
    }
}
