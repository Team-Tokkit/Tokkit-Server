package com.example.Tokkit_server.store.service.command;

import com.example.Tokkit_server.store.dto.response.KakaoMapSearchResponse;

import java.util.List;
public interface StoreCommandService {
    public List<KakaoMapSearchResponse> findNearbyStores(
            Double lat,
            Double lng,
            Integer radius,
            String storeCategory,
            String keyword
    );
}
