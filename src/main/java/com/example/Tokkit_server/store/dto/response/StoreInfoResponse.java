package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.store.entity.Store;

import lombok.Getter;

@Getter
public class StoreInfoResponse {
    private Long storeId;
    private Long merchantId;
    private String storeName;
    private String address;
    private String merchantName;

    public StoreInfoResponse(Store store, Long merchantId) {
        this.storeId = store.getId();
        this.merchantId = merchantId;
        this.storeName = store.getStoreName();
        this.address = store.getRoadAddress();
        this.merchantName = store.getMerchant().getName();
    }
}