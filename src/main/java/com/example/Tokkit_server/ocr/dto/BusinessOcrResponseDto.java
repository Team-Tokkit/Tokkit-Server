package com.example.Tokkit_server.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessOcrResponseDto {
    private String businessNumber;
    private String storeName;
    private String representativeName;
    private String roadAddress;

    public static BusinessOcrResponseDto of(String bizNum, String store, String rep, String addr) {
        return BusinessOcrResponseDto.builder()
                .businessNumber(bizNum)
                .storeName(store)
                .representativeName(rep)
                .roadAddress(addr)
                .build();
    }
}
