package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.global.utils.MultipartInputStreamFileResource;
import com.example.Tokkit_server.merchant.dto.response.BusinessOcrResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessOcrService {

    @Value("${naver.ocr.business.secret-key}")
    private String secretKey;

    @Value("${naver.ocr.business.invoke-url}")
    private String invokeUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public BusinessOcrResponseDto extractBusinessInfo(MultipartFile image) {
        try {
            log.info("[OCR] 사업자등록증 OCR 시작");

            // 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            // 요청 바디
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", objectMapper.writeValueAsString(defaultRequestBody()));
            body.add("file", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("[OCR] Naver OCR API 요청 시작");
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    invokeUrl,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            );

            JsonNode result = response.getBody();
            log.info("[OCR] 응답 수신 완료: {}", result);

            if (result == null || !result.has("images")) {
                throw new GeneralException(ErrorStatus.OCR_PROCESSING_FAILED);
            }

            JsonNode bizResult = result.get("images").get(0)
                    .path("bizLicense")
                    .path("result");

            String registerNumber = getTextOrNull(bizResult, "registerNumber");
            String companyName = getTextOrNull(bizResult, "companyName");
            String repName = getTextOrNull(bizResult, "repName");
            String address = getTextOrNull(bizResult, "bisAddress");

            return BusinessOcrResponseDto.of(registerNumber, companyName, repName, address);

        } catch (Exception e) {
            log.error("[OCR] OCR 처리 중 예외 발생", e);
            throw new GeneralException(ErrorStatus.OCR_PROCESSING_FAILED);
        }
    }

    private String getTextOrNull(JsonNode parent, String field) {
        if (parent != null && parent.has(field)) {
            JsonNode array = parent.get(field);
            if (array.isArray() && array.size() > 0 && array.get(0).has("text")) {
                return array.get(0).get("text").asText();
            }
        }
        return null;
    }

    private Map<String, Object> defaultRequestBody() {
        Map<String, Object> image = new HashMap<>();
        image.put("format", "jpg");
        image.put("name", "biz-license");

        Map<String, Object> request = new HashMap<>();
        request.put("images", new Object[]{image});
        request.put("requestId", java.util.UUID.randomUUID().toString());
        request.put("version", "V2");
        request.put("timestamp", System.currentTimeMillis());

        return request;
    }
}
