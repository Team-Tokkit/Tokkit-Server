package com.example.Tokkit_server.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoAddressResponseDto {
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    public static class Document {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("x")
        private String longitude;

        @JsonProperty("y")
        private String latitude;
    }
}

