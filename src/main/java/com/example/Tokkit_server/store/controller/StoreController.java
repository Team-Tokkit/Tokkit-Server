package com.example.Tokkit_server.store.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
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
    public ApiResponse<List<StoreResponse>> getNearbyStores(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") double radius) {

        List<StoreResponse> response = storeCommandService.getStoresByRadius(lat, lng, radius);
        return (ApiResponse.onSuccess(response));
    }
}