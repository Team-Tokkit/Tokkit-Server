package com.example.Tokkit_server;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Store;
import com.example.Tokkit_server.store.service.command.StoreCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class StoreCommandServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreCommandService storeCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStoresByRadius_Success() {
        // given
        double lat = 37.5665;
        double lng = 126.9780;
        double radius = 1000;

        // Mock Store 객체 생성
        Store mockStore = Store.builder()
                .storeName("Test Store")
                .roadAddress("Test Address")
                .longitude(126.9780)
                .latitude(37.5665)
                .build();
        List<Store> mockStores = List.of(mockStore);

        // Mocking
        when(storeRepository.findNearbyStoresByUserVoucher(1L, lat, lng, radius)).thenReturn(mockStores);

        // when
        List<StoreResponse> result = storeCommandService.getStoresByRadius(lat, lng, radius);

        // then
        assertNotNull(result);
        assertEquals(1, result.size()); // 반환된 StoreResponse 리스트 크기 확인
        verify(storeRepository, times(1)).findNearbyStoresByUserVoucher(1L, lat, lng, radius); // 메서드 호출 확인
    }

    @Test
    void getStoresByRadius_Failure_InvalidRadius() {
        // given
        double lat = 37.5665;
        double lng = 126.9780;
        double radius = -100;

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            storeCommandService.getStoresByRadius(lat, lng, radius);
        });

        assertEquals(ErrorStatus.INVALID_RADIUS, exception.getErrorStatus());
        verify(storeRepository, never()).findNearbyStoresByUserVoucher(anyLong(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void getStoresByRadius_Success_NoStoresFound() {
        // given
        double lat = 37.5665;
        double lng = 126.9780;
        double radius = 1000;

        when(storeRepository.findNearbyStoresByUserVoucher(1L, lat, lng, radius)).thenReturn(Collections.emptyList());

        // when
        List<StoreResponse> result = storeCommandService.getStoresByRadius(lat, lng, radius);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(storeRepository, times(1)).findNearbyStoresByUserVoucher(1L, lat, lng, radius);
    }
}