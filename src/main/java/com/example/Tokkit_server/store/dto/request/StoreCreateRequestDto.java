package com.example.Tokkit_server.store.dto.request;

import lombok.Getter;

@Getter
public class StoreCreateRequestDto {
    private String storeName;
    private String roadAddress;
    private String newZipcode;

    private Double latitude;
    private Double longitude;

    private Long categoryId;
    private Long regionId;
    private Long merchantId;
}
