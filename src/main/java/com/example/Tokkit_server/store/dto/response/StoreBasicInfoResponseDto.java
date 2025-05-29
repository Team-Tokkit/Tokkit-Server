package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;

public record StoreBasicInfoResponseDto(
	Long storeId,
	String storeName,
	String category,
	String address,
	String postalCode
) {
	public static StoreBasicInfoResponseDto from(Store store) {
		return new StoreBasicInfoResponseDto(
			store.getId(),
			store.getStoreName(),
			store.getStoreCategory().name(),
			store.getRoadAddress(),
			store.getNewZipcode()
		);
	}
}
