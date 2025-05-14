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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            JsonNode images = root.path("images");
            if (!images.isArray() || images.size() == 0) {
                throw new GeneralException(ErrorStatus.OCR_PROCESSING_FAILED);
            }

            JsonNode idCardRoot = images.get(0).path("idCard").path("result");
            JsonNode result = idCardRoot.has("dl") ? idCardRoot.path("dl") : idCardRoot.path("ic");

            String name = extractFormattedOrText(result.path("name"));
            String rrnFull = extractFormattedOrText(result.path("personalNum"));
            String rrnClean = rrnFull != null ? rrnFull.replaceAll("-", "") : null;
            String rrnFront = rrnClean != null && rrnClean.length() >= 7 ? rrnClean.substring(0, 7) : null;

            String issuedDate = extractDateAsCompactString(result.path("issueDate"), result.path("condition"));

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

    /**
     * 발급일자 추출: yyyyMMdd 형식으로 리턴 (예: 20240509)
     * year 누락 시 condition.text에서 보완, month/day는 text 또는 formatted에서 추출
     */
    private String extractDateAsCompactString(JsonNode issueDateNode, JsonNode conditionNode) {
        String year = "";
        String month = "";
        String day = "";

        // 1. issueDate.text에서 MM.DD 패턴 추출
        if (issueDateNode.isArray() && issueDateNode.size() > 0) {
            String text = issueDateNode.get(0).path("text").asText("");
            Matcher mdMatcher = Pattern.compile("(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])").matcher(text);
            if (mdMatcher.find()) {
                String[] parts = mdMatcher.group().split("\\.");
                month = parts[0];
                day = parts[1];
            }

            // formatted에서 year가 있다면 우선 사용
            JsonNode formatted = issueDateNode.get(0).path("formatted");
            if (formatted.has("year")) {
                year = formatted.path("year").asText("");
            }
        }

        // 2. year 누락 시 condition.text에서 보완
        if (year.isBlank() && conditionNode.isArray() && conditionNode.size() > 0) {
            String conditionText = conditionNode.get(0).path("text").asText("");
            Matcher yMatcher = Pattern.compile("\\b(19|20)\\d{2}\\b").matcher(conditionText);
            if (yMatcher.find()) {
                year = yMatcher.group();
            }
        }

        // 3. 모두 갖췄으면 yyyyMMdd로 리턴
        if (!year.isBlank() && !month.isBlank() && !day.isBlank()) {
            return year + month + day;
        }

        // 4. 실패 시 "" 반환
        return "";
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
