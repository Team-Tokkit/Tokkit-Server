package com.example.Tokkit_server.region.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigunguResponse {

    private String sigunguName;

    public static SigunguResponse from(String sigunguName) {
        return SigunguResponse.builder()
                .sigunguName(sigunguName)
                .build();
    }
}

