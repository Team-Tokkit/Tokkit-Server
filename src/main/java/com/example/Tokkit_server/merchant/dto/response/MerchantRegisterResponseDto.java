package com.example.Tokkit_server.merchant.dto.response;

import com.example.Tokkit_server.merchant.entity.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MerchantRegisterResponseDto {
    public Long id;
    public String storeName;

    public static MerchantRegisterResponseDto from(Merchant merchant) {
        return MerchantRegisterResponseDto.builder()
                .id(merchant.getId())
                .storeName(merchant.getStore().getStoreName())
                .build();
    }
}
