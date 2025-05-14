package com.example.Tokkit_server.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.store.dto.response.StoreInfoResponse;
import com.example.Tokkit_server.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/info")
    @Operation(summary = "QR 기반 가맹점 정보 조회", description = "가맹점주 ID와 매장 ID를 기반으로 가맹점 정보를 조회합니다.")
    public ApiResponse<StoreInfoResponse> getStoreInfo(
            @RequestParam Long merchantId,
            @RequestParam Long storeId) {
        return ApiResponse.onSuccess(storeService.getStoreInfo(merchantId, storeId));
    }
}