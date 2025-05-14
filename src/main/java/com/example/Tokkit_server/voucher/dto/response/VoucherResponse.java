package com.example.Tokkit_server.voucher.dto.response;

import com.example.Tokkit_server.voucher.entity.Voucher;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime validDate;
    private String contact;
    private Integer remainingCount;
    private String imageUrl;


    public static VoucherResponse from (Voucher voucher, String imageProxyBaseUrl) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .originalPrice(voucher.getOriginalPrice())
                .validDate(voucher.getValidDate())
                .contact(voucher.getContact())
                .remainingCount(voucher.getRemainingCount())
                .imageUrl(
                        voucher.getImage() != null ?
                                imageProxyBaseUrl + voucher.getImage().getImageUrl()
                                : null
                )
                .build();
    }
}
