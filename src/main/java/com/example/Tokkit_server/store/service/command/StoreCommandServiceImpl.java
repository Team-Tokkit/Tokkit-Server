package com.example.Tokkit_server.store.service.command;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.KakaoMapSearchResponse;
import com.example.Tokkit_server.store.enums.StoreCategory;
import com.example.Tokkit_server.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreCommandServiceImpl implements StoreCommandService {
    private final StoreRepository storeRepository;

    @Override
    public List<KakaoMapSearchResponse> findNearbyStores(
            Double lat,
            Double lng,
            Integer radius,
            String storeCategory,
            String keyword
    ) {
        if (lat == null || lat < -90 || lat > 90) {
            throw new GeneralException(ErrorStatus.INVALID_LATITUDE);
        }
        if (radius == null || radius < 1) {
            throw new GeneralException(ErrorStatus.INVALID_RADIUS);
        }
        StoreCategory category = null;

        if (storeCategory != null) {
            category = Arrays.stream(StoreCategory.values())
                    .filter(cat -> cat.getKoreanName().equals(storeCategory))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_CATEGORY_NOT_FOUND));
        }

        Long userId = 1L; // TODO: JWT로 대체 예정

        List<Object[]> results = storeRepository.findNearbyStoresRaw(
                userId,
                lat,
                lng,
                radius,
                category != null ? category.name() : null,
                keyword
        );

        return results.stream()
                .map(row -> new KakaoMapSearchResponse(
                        ((Long) row[0]),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Double) row[4]),
                        ((Double) row[5]),
                        ((Double) row[7]),
                        StoreCategory.valueOf((String) row[6])
                ))
                .collect(Collectors.toList());
    }
}
