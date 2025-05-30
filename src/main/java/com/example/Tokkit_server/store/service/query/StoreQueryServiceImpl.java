package com.example.Tokkit_server.store.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.store.dto.response.StoreBasicInfoResponseDto;
import com.example.Tokkit_server.store.dto.response.VoucherPageResponseDto;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.repository.VoucherOwnershipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreQueryServiceImpl implements StoreQueryService {

	private final StoreRepository storeRepository;
	private final VoucherOwnershipRepository voucherOwnershipRepository;
	private final UserRepository userRepository;

	@Override
	public StoreBasicInfoResponseDto getStoreInfo(Long storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
		return StoreBasicInfoResponseDto.from(store);
	}

	@Override
	public VoucherPageResponseDto getAvailableVouchers(Long storeId, Long userId, Pageable pageable) {

		getStoreInfo(storeId);

		userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		Page<VoucherOwnership> page = voucherOwnershipRepository.findAvailableVouchersByUserAndStore(
			userId, storeId, pageable
		);

		return VoucherPageResponseDto.from(page);
	}
}
