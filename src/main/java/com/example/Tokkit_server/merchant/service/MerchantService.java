package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.dto.request.CreateMerchantRequestDto;
import com.example.Tokkit_server.merchant.dto.request.MerchantEmailChangeRequestDto;
import com.example.Tokkit_server.merchant.dto.request.UpdateMerchantPasswordRequestDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantRegisterResponseDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantResponseDto;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.entity.MerchantEmailValidation;
import com.example.Tokkit_server.merchant.repository.MerchantEmailValidationRepository;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.ocr.service.KakaoAddressSearchService;
import com.example.Tokkit_server.ocr.utils.KakaoGeoResult;
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

import java.util.Date;
import java.util.Optional;

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
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    // 회원가입
    @Transactional
    public MerchantRegisterResponseDto createMerchant(CreateMerchantRequestDto request) {
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

        // 3. 사업자 등록 번호 중복 체크
        if (merchantRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new GeneralException(ErrorStatus.MERCHANT_ALREADY_EXISTS);
        }

        // 4. Region 조회
        Region region = regionRepository.findBySidoNameAndSigunguName(
                        request.getSidoName(), request.getSigunguName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND));

        // 5. Merchant 저장
        Merchant merchant = request.toMerchantEntity(passwordEncoder);
        merchantRepository.save(merchant);

        // 6. 도로명 주소로 Kakao 주소 API 호출 (위도, 경도, 우편번호 조회)
        Optional<KakaoGeoResult> geoResultOpt = kakaoAddressSearchService.search(request.getRoadAddress());

        KakaoGeoResult kakaoGeoResult = geoResultOpt
                .orElseThrow(() -> new GeneralException(ErrorStatus.ADDRESS_NOT_FOUND));

        Coordinate coord = new Coordinate(kakaoGeoResult.getLongitude(), kakaoGeoResult.getLatitude());
        Point point = geometryFactory.createPoint(coord);


        // 7. Store 저장 - Kakao 응답 정보로 채움
        Store store = Store.builder()
                .storeName(request.getStoreName())
                .roadAddress(request.getRoadAddress())
                .newZipcode(kakaoGeoResult.getZipCode())
                .latitude(kakaoGeoResult.getLatitude())
                .longitude(kakaoGeoResult.getLongitude())
                .location(point)
                .merchant(merchant)
                .region(region)
                .storeCategory(request.getStoreCategory())
                .build();
        storeRepository.save(store);
        merchant.setStore(store);

        // 8. Wallet 생성
        Wallet wallet = walletCommandService.createInitialWalletForMerchant(merchant.getId());
        merchant.setWallet(wallet);

        return MerchantRegisterResponseDto.from(merchant);
    }


    // 내 정보 조회
    public MerchantResponseDto getInfo(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));
        return MerchantResponseDto.from(merchant);
    }

    // 비밀번호 변경
    @Transactional
    public MerchantResponseDto updateMerchantPassword(String email, UpdateMerchantPasswordRequestDto requestDto) {
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        if (requestDto.getPassword() == null || requestDto.getNewPassword() == null) {
            throw new GeneralException(ErrorStatus.MERCHANT_PASSWORD_UPDATE_BAD_REQUEST);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), merchant.getPassword())) {
            throw new GeneralException(ErrorStatus.MERCHANT_PASSWORD_NOT_MATCH);
        }

        // 새로운 비밀번호 인코딩 및 업데이트
        String encodedNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
        merchant.updatePassword(encodedNewPassword);

        merchantRepository.save(merchant);
        return MerchantResponseDto.from(merchant);
    }

    // 간편 비밀번호 변경 시 이메일 인증
    @Transactional
    public void verifySimplePassword(String email, String simplePassword) {
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        boolean matches = merchant.matchSimplePassword(passwordEncoder, simplePassword);
        if (!matches) {
            throw new GeneralException(ErrorStatus.INVALID_SIMPLE_PASSWORD);
        }
    }

    // 간편 비밀번호 변경
    @Transactional
    public void updateSimplePassword(String email, String newSimplePassword) {
        Merchant merchant = merchantRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        merchant.updateSimplePassword(passwordEncoder.encode(newSimplePassword));
    }

    // 이메일 변경
    @Transactional
    public void updateEmail(Long merchantId, MerchantEmailChangeRequestDto requestDto) {
        MerchantEmailValidation validation = emailValidationRepository.findById(requestDto.getNewEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED));

        if (!validation.getIsVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MERCHANT_NOT_FOUND));

        merchant.updateEmail(requestDto.getNewEmail());
    }
}
