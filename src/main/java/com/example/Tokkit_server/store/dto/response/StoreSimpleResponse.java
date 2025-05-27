package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreSimpleResponse {
    private Long storeId;
    private String storeName;
    private String address;

    public static StoreSimpleResponse from(Store store) {
        return StoreSimpleResponse.builder()
            .storeId(store.getId())
            .storeName(store.getStoreName())
            .address(store.getRoadAddress())
            .build();
    }
}