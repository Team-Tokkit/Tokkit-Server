package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.Voucher;
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
    private LocalDateTime validDate;
    private String contact;

    public static VoucherResponse from (Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .validDate(voucher.getValidDate())
                .contact(voucher.getContact())
                .build();
    }
}
