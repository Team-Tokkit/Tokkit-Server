package com.example.Tokkit_server.ocr.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.ocr.dto.IdCardOcrResponseDto;
import com.example.Tokkit_server.ocr.utils.MultipartInputStreamFileResource;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdCardOcrService {

    @Value("${naver.ocr.idCard.secret-key}")
    private String secretKey;

    @Value("${naver.ocr.idCard.invoke-url}")
    private String invokeUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public IdCardOcrResponseDto extractInfo(MultipartFile image) {
        try {
            log.info("[신분증 OCR] 요청 시작");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", objectMapper.writeValueAsString(defaultRequestBody()));
            body.add("file", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("[신분증 OCR] Naver API 요청 전송");
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    invokeUrl,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            );

            JsonNode root = response.getBody();
            log.info("[신분증 OCR] 응답 수신: {}", root.toPrettyString());

            JsonNode images = root.path("images");
            if (!images.isArray() || images.size() == 0) {
                throw new GeneralException(ErrorStatus.OCR_PROCESSING_FAILED);
            }

            JsonNode idCardRoot = images.get(0).path("idCard").path("result");
            JsonNode result = idCardRoot.has("dl") ? idCardRoot.path("dl") : idCardRoot.path("ic");

            String name = extractFormattedOrText(result.path("name"));
            String rrnFull = extractFormattedOrText(result.path("personalNum"));
            String rrnFront = rrnFull != null && rrnFull.length() >= 8 ? rrnFull.substring(0, 8) : null;
            String issuedDate = extractFormattedOrText(result.path("issueDate"));

            log.info("[신분증 OCR] 추출 결과 - name: {}, rrnFront: {}, issuedDate: {}", name, rrnFront, issuedDate);

            return IdCardOcrResponseDto.builder()
                    .name(name)
                    .rrnFront(rrnFront)
                    .issuedDate(issuedDate)
                    .build();

        } catch (Exception e) {
            log.error("[신분증 OCR 실패] {}", e.getMessage(), e);
            throw new GeneralException(ErrorStatus.OCR_PROCESSING_FAILED);
        }
    }

    private String extractFormattedOrText(JsonNode node) {
        if (node.isArray() && node.size() > 0) {
            JsonNode first = node.get(0);
            JsonNode formatted = first.path("formatted").path("value");
            if (!formatted.isMissingNode() && !formatted.isNull()) {
                return formatted.asText();
            }
            return first.path("text").asText(null);
        }
        return null;
    }

    private Map<String, Object> defaultRequestBody() {
        Map<String, Object> image = new HashMap<>();
        image.put("format", "jpg");
        image.put("name", "id-card");

        Map<String, Object> request = new HashMap<>();
        request.put("images", new Object[]{image});
        request.put("requestId", UUID.randomUUID().toString());
        request.put("version", "V2");
        request.put("timestamp", System.currentTimeMillis());

        return request;
    }
}
