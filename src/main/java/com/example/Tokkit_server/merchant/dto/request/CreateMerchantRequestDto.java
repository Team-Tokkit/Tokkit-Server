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
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String simplePassword;
    private String businessNumber;

    // 가맹점 정보
    private String storeName;
    private String roadAddress;
    private String newZipcode;

    private String sidoName;
    private String sigunguName;

    private StoreCategory storeCategory;

    // Merchant 엔티티 생성
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

    // Store 엔티티 생성
    public Store toStoreEntity(Merchant merchant, Region region, StoreCategory category,
                               double latitude, double longitude, Point point) {
        return Store.builder()
                .storeName(storeName)
                .roadAddress(roadAddress)
                .newZipcode(newZipcode)
                .latitude(latitude)
                .longitude(longitude)
                .location(point)
                .merchant(merchant)
                .region(region)
                .storeCategory(category)
                .build();
    }
}
