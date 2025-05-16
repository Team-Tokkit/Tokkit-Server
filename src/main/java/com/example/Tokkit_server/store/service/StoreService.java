package com.example.Tokkit_server.store.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.region.repository.RegionRepository;
import com.example.Tokkit_server.store.dto.request.StoreCreateRequestDto;
import com.example.Tokkit_server.store.dto.response.StoreInfoResponse;
import com.example.Tokkit_server.store.dto.response.StoreSimpleResponse;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StoreService {
	private final MerchantRepository merchantRepository;
	private final RegionRepository regionRepository;
	private final StoreRepository storeRepository;

	private final WalletRepository walletRepository;

	/**
	 * 상점 생성 서비스 로직
	 */
	public void createStore(StoreCreateRequestDto dto) {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));

		Store store = Store.builder()
			.storeName(dto.getStoreName())
			.roadAddress(dto.getRoadAddress())
			.newZipcode(dto.getNewZipcode())
			.longitude(dto.getLongitude())
			.latitude(dto.getLatitude())
			.storeCategory(dto.getStoreCategory())
			.region(
				regionRepository.findById(dto.getRegionId())
					.orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND))
			)
			.merchant(
				merchantRepository.findById(dto.getMerchantId())
					.orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND))
			)
			.location(location)
			.build();

		storeRepository.save(store);
	}


	public StoreInfoResponse getStoreInfo(Long merchantId, Long storeId) {
		Store store = storeRepository.findByIdAndMerchantId(storeId, merchantId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
		return new StoreInfoResponse(store);
	}





	public StoreSimpleResponse getSimpleStoreInfo(Long storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));

		return StoreSimpleResponse.from(store);
	}

}
