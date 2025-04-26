package com.example.Tokkit_server.dto.voucher;

import com.example.Tokkit_server.domain.Voucher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherRequestDto {

    private String name;
    private String description;
    private Voucher.Category category;
    private Integer price;
    private Integer supportAmount;
    private Integer totalSupportAmount;
    private LocalDate validDate;
    private String contact;
    private Long merchantId;

}
