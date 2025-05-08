package com.example.Tokkit_server.merchant.dto.request;

import lombok.Getter;

@Getter
public class MerchantEmailVerificatioRequestDto {
    String email;
    String verification;
}
