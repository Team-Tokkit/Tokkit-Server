package com.example.Tokkit_server.merchant.dto.request;

import lombok.Data;

@Data
public class MerchantLoginRequestDto {
    private String businessNumber;
    private String password;
}
