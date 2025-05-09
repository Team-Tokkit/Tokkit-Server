package com.example.Tokkit_server.store.service.command;

import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.KakaoMapStoreResponse;
import com.example.Tokkit_server.store.enums.StoreCategory;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreCommandServiceImpl implements StoreCommandService {
    private final StoreRepository storeRepository;

    @Override
    public List<KakaoMapStoreResponse> findNearbyStores(double lat, double lng, double radius, StoreCategory category, String keyword) {
        if (radius <= 0) {
            throw new GeneralException(ErrorStatus.INVALID_RADIUS);
        }

        // 이미 KakaoMapStoreResponse를 반환하므로 추가 변환 불필요
        return storeRepository.findNearbyStores(
                1L, // userId는 필요 시 동적으로 변경
                lat,
                lng,
                radius,
                category != null ? category.name() : null,
                keyword
        );
    }
}
