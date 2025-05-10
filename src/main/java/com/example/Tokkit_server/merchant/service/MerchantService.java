package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.dto.request.CreateMerchantRequestDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantResponseDto;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.entity.MerchantEmailValidation;
import com.example.Tokkit_server.merchant.repository.MerchantEmailValidationRepository;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.region.entity.Region;
import com.example.Tokkit_server.region.repository.RegionRepository;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.repository.StoreRepository;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.wallet.service.command.WalletCommandService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantEmailValidationRepository emailValidationRepository;
    private final RegionRepository regionRepository;
    private final StoreRepository storeRepository;
    private final WalletCommandService walletCommandService;
    private final PasswordEncoder passwordEncoder;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public MerchantResponseDto createMerchant(CreateMerchantRequestDto request) {
        // 1. 이메일 인증 여부 확인
        MerchantEmailValidation emailValidation = emailValidationRepository
                .findTopByEmailOrderByExpDesc(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!emailValidation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        // 2. 이메일 중복 체크
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new GeneralException(ErrorStatus.MERCHANT_ALREADY_EXISTS);
        }

        // 3. Region, StoreCategory 조회
        Region region = regionRepository.findBySidoNameAndSigunguName(
                        request.getSidoName(), request.getSigunguName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND));

        // 4. Merchant 저장
        Merchant merchant = request.toMerchantEntity(passwordEncoder);
        merchantRepository.save(merchant);

        // 5. 좌표로 Point 생성
        Coordinate coord = new Coordinate(request.getLongitude(), request.getLatitude());
        Point point = geometryFactory.createPoint(coord);

        // 6. Store 저장
        Store store = request.toStoreEntity(
                merchant,
                region,
                request.getStoreCategory(),
                point
        );
        storeRepository.save(store);

        // 7. Wallet 생성
        Wallet wallet = walletCommandService.createInitialWalletForMerchant(merchant.getId());
        merchant.setWallet(wallet);

        return MerchantResponseDto.from(merchant);
    }
}
