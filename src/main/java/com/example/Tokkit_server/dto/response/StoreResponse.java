package com.example.Tokkit_server.dto.response;

import com.example.Tokkit_server.domain.Store;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String address;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .build();
    }
}
