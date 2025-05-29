package com.example.Tokkit_server.store.service.query;
import com.example.Tokkit_server.store.dto.response.StoreBasicInfoResponseDto;
import com.example.Tokkit_server.store.dto.response.VoucherPageResponseDto;

import org.springframework.data.domain.Pageable;
public interface StoreQueryService {
	StoreBasicInfoResponseDto getStoreInfo(Long storeId);
	VoucherPageResponseDto getAvailableVouchers(Long storeId, Long userId, Pageable pageable);
}
