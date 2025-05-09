package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;

import lombok.Getter;

@Getter
public class StoreInfoResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String merchantName;

    public StoreInfoResponse(Store store) {
        this.storeId = store.getId();
        this.storeName = store.getStoreName();
        this.address = store.getRoadAddress();
        this.merchantName = store.getMerchant().getName();
    }
}