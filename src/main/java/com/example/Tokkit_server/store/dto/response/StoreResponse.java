package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreResponse {
    private Long id;
    private String storeName;
    private String roadAddress;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .roadAddress(store.getRoadAddress())
                .build();
    }
}
