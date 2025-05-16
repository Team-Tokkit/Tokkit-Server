package com.example.Tokkit_server.ocr.utils;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoGeoClient {

    @Value("${kakao.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoGeoResult searchAddress(String roadAddress) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", roadAddress);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );

        JsonNode meta = response.getBody().path("meta");
        if (meta.path("total_count").asInt() == 0) {
            throw new GeneralException(ErrorStatus.ADDRESS_NOT_FOUND);
        }

        JsonNode doc = response.getBody().path("documents").get(0);
        Double latitude = Double.valueOf(doc.path("y").asText());
        Double longitude = Double.valueOf(doc.path("x").asText());
        String zipCode = doc.path("road_address").path("zone_no").asText();

        return new KakaoGeoResult(longitude, latitude, zipCode);
    }
}
