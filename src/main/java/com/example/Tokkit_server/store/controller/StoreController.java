package com.example.Tokkit_server.store.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.store.dto.response.KakaoMapSearchResponse;
import com.example.Tokkit_server.store.dto.response.StoreInfoResponse;
import com.example.Tokkit_server.store.dto.response.StoreSimpleResponse;
import com.example.Tokkit_server.store.service.StoreService;
import com.example.Tokkit_server.store.service.command.StoreCommandService;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")

public class StoreController {

    private final StoreCommandService storeCommandService;
    private final StoreService storeService;

    @GetMapping("/nearby")
    @Operation(
            summary = "근처 가맹점 조회",
            description = "사용자의 위치, 반경, 키워드, 카테고리를 기반으로 근처 가맹점을 조회합니다.\n\n"
                    + "[storeCategory 가능한 값]\n"
                    + "- 음식점\n"
                    + "- 의료\n"
                    + "- 서비스\n"
                    + "- 관광\n"
                    + "- 숙박\n"
                    + "- 교육",
            tags = {"Store"}
    )
    public ApiResponse<List<KakaoMapSearchResponse>> findNearbyStores(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng,
            @RequestParam(value = "radius", required = false) Integer radius,
            @RequestParam(value = "storeCategory", required = false) String storeCategory,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.onSuccess(
                storeCommandService.findNearbyStores(userDetails.getId(),lat, lng, radius, storeCategory, keyword)
        );
    }


    @GetMapping("/info")
    @Operation(summary = "QR 기반 가맹점 정보 조회", description = "가맹점주 ID와 매장 ID를 기반으로 가맹점 정보를 조회합니다.")
    public ApiResponse<StoreInfoResponse> getStoreInfo(
            @RequestParam Long merchantId,
            @RequestParam Long storeId) {
        return ApiResponse.onSuccess(storeService.getStoreInfo(merchantId, storeId));
    }

    @GetMapping("/simple")
    @Operation(summary = "일반 가맹점 단건 조회", description = "storeId를 기반으로 가맹점 정보를 조회합니다.")
    public ApiResponse<StoreSimpleResponse> getStoreSimpleInfo(@RequestParam Long storeId) {
        StoreSimpleResponse response = storeService.getSimpleStoreInfo(storeId);
        return ApiResponse.onSuccess(response);
    }
}
