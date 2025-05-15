package com.example.Tokkit_server.region.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidoResponse {

    private String sidoName;

    public static SidoResponse from(String sidoName) {
        return SidoResponse.builder()
                .sidoName(sidoName)
                .build();
    }
}
