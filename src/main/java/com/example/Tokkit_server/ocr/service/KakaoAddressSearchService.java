package com.example.Tokkit_server.ocr.service;

import com.example.Tokkit_server.ocr.utils.KakaoGeoResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAddressSearchService {

    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.address-search-url}")
    private String searchUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public Optional<KakaoGeoResult> search(String roadAddress) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(searchUrl)
                .queryParam("query", roadAddress)
                .build();

        try {

            ResponseEntity<String> response = restTemplate.exchange(
                    uri.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );


            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            if (documents.isArray() && documents.size() > 0) {
                JsonNode first = documents.get(0);
                String x = first.path("x").asText();
                String y = first.path("y").asText();
                String zipCode = first.path("road_address").path("zone_no").asText();

                double longitude = Double.parseDouble(x);
                double latitude = Double.parseDouble(y);


                return Optional.of(new KakaoGeoResult(longitude, latitude, zipCode));
            } else {
            }
        } catch (Exception e) {
        }

        return Optional.empty();
    }

}
