package com.example.Tokkit_server.merchant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessOcrResponseDto {
    private String businessNumber;   // 사업자등록번호
    private String storeName;        // 상호
    private String ownerName;        // 대표자명
    private String roadAddress;      // 사업장주소

    public static BusinessOcrResponseDto of(String businessNumber, String storeName, String ownerName, String roadAddress) {
        return BusinessOcrResponseDto.builder()
                .businessNumber(businessNumber)
                .storeName(storeName)
                .ownerName(ownerName)
                .roadAddress(roadAddress)
                .build();
    }
}
