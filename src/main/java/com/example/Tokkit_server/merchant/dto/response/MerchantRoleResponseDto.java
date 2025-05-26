package com.example.Tokkit_server.merchant.dto.response;

import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MerchantRoleResponseDto {
    private String roles;

    public static MerchantRoleResponseDto of(CustomMerchantDetails merchantDetails) {
        return MerchantRoleResponseDto.builder()
                .roles(merchantDetails.getRole())
                .build();
    }
}
