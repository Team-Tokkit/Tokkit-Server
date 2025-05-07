package com.example.Tokkit_server.store.service.command;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreCommandServiceImpl implements StoreCommandService {
    private final StoreRepository storeRepository;

    public List<StoreResponse> getStoresByRadius(double lat, double lng, double radius) {
        if (radius <= 0) {
            throw new GeneralException(ErrorStatus.INVALID_RADIUS);
        }

        List<Store> stores = storeRepository.findNearbyStoresByUserVoucher(1l,lat, lng, radius);
        return stores.stream().map(StoreResponse::from).collect(Collectors.toList());
    }
}
