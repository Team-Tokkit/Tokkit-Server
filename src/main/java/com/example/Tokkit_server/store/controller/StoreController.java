package com.example.Tokkit_server.store.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.store.dto.response.KakaoMapStoreResponse;
import com.example.Tokkit_server.store.enums.StoreCategory;
import com.example.Tokkit_server.store.service.command.StoreCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCommandService storeCommandService;

    @GetMapping("/nearby")
    public ApiResponse<List<KakaoMapStoreResponse>> findNearbyStores(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int radius,
            @RequestParam(required = false) StoreCategory
                    category,
            @RequestParam(required = false) String keyword) {

        List<KakaoMapStoreResponse> stores = storeCommandService.findNearbyStores(lat, lng, radius, category, keyword);
        return ApiResponse.onSuccess(stores);
    }
}