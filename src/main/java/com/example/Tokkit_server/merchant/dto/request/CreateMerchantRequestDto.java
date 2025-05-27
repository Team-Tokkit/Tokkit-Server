package com.example.Tokkit_server.merchant.dto.request;

import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.region.entity.Region;
import com.example.Tokkit_server.store.entity.Store;
import com.example.Tokkit_server.store.enums.StoreCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantRequestDto {

    // 가맹점주 정보
    private String name;                  // 대표자명 (OCR)
    private String email;
    private String phoneNumber;
    private String password;
    private String simplePassword;
    private String businessNumber;       // 사업자등록번호 (OCR)

    // 가맹점 정보
    private String storeName;            // 상호명 (OCR)
    private String roadAddress;          // 도로명주소 (카카오 API 결과)

    private String sidoName;             // 시/도 (사용자 선택)
    private String sigunguName;          // 시/군/구 (사용자 선택)
    private StoreCategory storeCategory; // 상점 카테고리 (사용자 선택)

    // 가맹점주 엔티티 생성
    public Merchant toMerchantEntity(PasswordEncoder encoder) {
        return Merchant.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(encoder.encode(password))
                .simplePassword(encoder.encode(simplePassword))
                .businessNumber(businessNumber)
                .isDormant(false)
                .roles("MERCHANT")
                .build();
    }

    // 가맹점 엔티티 생성
    public Store toStoreEntity(Merchant merchant, Region region, StoreCategory category, Point point) {
        return Store.builder()
                .storeName(storeName)
                .roadAddress(roadAddress)
                .location(point)
                .merchant(merchant)
                .region(region)
                .storeCategory(category)
                .build();
    }
}
