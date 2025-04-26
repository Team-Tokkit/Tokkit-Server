package com.example.Tokkit_server.dto.voucher;

import com.example.Tokkit_server.domain.Voucher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponseDto {

    private String name;
    private String description;
    private Integer price;
    private String validDate;


    public static VoucherResponseDto from(Voucher voucher) {
        return VoucherResponseDto.builder()
                .name(voucher.getName())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .validDate(voucher.getValidDate().toString())
                .build();
    }

}
