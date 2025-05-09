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
public class MerchantResponseDto {

    public Long id;
    public String name;
    public String email;
    public String phoneNumber;
    public String accountNumber;

    public static MerchantResponseDto from(Merchant merchant) {
        return MerchantResponseDto.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .phoneNumber(merchant.getPhoneNumber())
                .accountNumber(merchant.getWallet().getAccountNumber())
                .build();
    }
}
