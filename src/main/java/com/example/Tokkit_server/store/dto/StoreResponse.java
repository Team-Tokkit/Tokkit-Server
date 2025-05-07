package com.example.Tokkit_server.store.dto;

import com.example.Tokkit_server.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String roadAddress;
    private String newZipCode;
    private double latitude;
    private double longitude;

    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getRoadAddress(),
                store.getNewZipcode(),
                store.getLatitude(),
                store.getLongitude()
        );
    }
}
