package com.example.Tokkit_server.ocr.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoGeoResult {
    private Double longitude;
    private Double latitude;
    private String zipCode;
}
