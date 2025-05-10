package com.example.Tokkit_server.merchant.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MerchantSimplePasswordVerificationRequestDto {
    private String code;
}
