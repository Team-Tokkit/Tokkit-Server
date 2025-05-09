package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMapStoreResponse {
    private Long id;
    private String name;
    private String roadAddress;
    private String newZipCode;
    private double latitude;
    private double longitude;
    private double distance;

    public static KakaoMapStoreResponse from(Store store , double distance) {
       return KakaoMapStoreResponse.builder()
                .id(store.getId())
                .name(store.getStoreName())
                .roadAddress(store.getRoadAddress())
                .newZipCode(store.getNewZipcode())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .distance(distance)
                .build();
    }
}
