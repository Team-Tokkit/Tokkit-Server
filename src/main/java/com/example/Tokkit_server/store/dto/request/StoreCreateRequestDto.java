package com.example.Tokkit_server.store.dto.request;

import com.example.Tokkit_server.store.enums.StoreCategory;

import lombok.Getter;

@Getter
public class StoreCreateRequestDto {
    private String storeName;
    private String roadAddress;
    private String newZipcode;

    private Double latitude;
    private Double longitude;

    private StoreCategory storeCategory;
    private Long regionId;
    private Long merchantId;
}
