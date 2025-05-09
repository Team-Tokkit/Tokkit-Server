package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.merchant.dto.request.CreateMerchantRequestDto;
import com.example.Tokkit_server.merchant.dto.response.KakaoAddressResponseDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


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
    private final GeometryFactory geometryFactory;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

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

        // 3. Kakao 주소 검색 API 호출 → 위도/경도 추출
        Coordinate coordinate = fetchCoordinateFromKakao(request.getRoadAddress());
        Point point = geometryFactory.createPoint(coordinate);

        // 4. Region, StoreCategory 조회
        Region region = regionRepository.findBySidoNameAndSigunguName(
                        request.getSidoName(), request.getSigunguName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND));

        // 5. Merchant 저장
        Merchant merchant = request.toMerchantEntity(passwordEncoder);
        merchantRepository.save(merchant);

        // 6. Store 저장
        Store store = request.toStoreEntity(
                merchant,
                region,
                request.getStoreCategory(),
                coordinate.y,
                coordinate.x,
                point
        );
        storeRepository.save(store);

        // 7. Wallet 생성
        Wallet wallet = walletCommandService.createInitialWalletForMerchant(merchant.getId());
        merchant.setWallet(wallet);

        return MerchantResponseDto.from(merchant);
    }

    private Coordinate fetchCoordinateFromKakao(String roadAddress) {
        // 1. URI 빌드 & 인코딩
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", roadAddress)
                .build()
                .encode();  // 인코딩까지 반드시!

        URI kakaoUri = uriComponents.toUri();

        System.out.println("📌 요청할 주소: " + roadAddress);
        System.out.println("📌 인코딩된 요청 URI: " + kakaoUri);

        // 2. 헤더 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 3. GET 요청 (URI 객체 사용)
        ResponseEntity<KakaoAddressResponseDto> response = restTemplate.exchange(
                kakaoUri,
                HttpMethod.GET,
                entity,
                KakaoAddressResponseDto.class
        );

        System.out.println("📌 카카오 API 응답 상태 코드: " + response.getStatusCode());

        // 4. 바디 파싱
        List<KakaoAddressResponseDto.Document> documents = response.getBody().getDocuments();
        System.out.println("📌 응답 document 수: " + documents.size());

        if (documents == null || documents.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_ADDRESS);
        }

        KakaoAddressResponseDto.Document doc = documents.get(0);
        double lng = Double.parseDouble(doc.getLongitude());
        double lat = Double.parseDouble(doc.getLatitude());

        System.out.println("📌 위도(Y): " + lat);
        System.out.println("📌 경도(X): " + lng);

        return new Coordinate(lng, lat);
    }

}
