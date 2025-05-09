package com.example.Tokkit_server.store.service.command;

import com.example.Tokkit_server.store.dto.response.KakaoMapStoreResponse;
import com.example.Tokkit_server.store.enums.StoreCategory;

import java.util.List;
public interface StoreCommandService {
    public List<KakaoMapStoreResponse> findNearbyStores(double lat, double lng, double radius, StoreCategory category, String keyword);
}
