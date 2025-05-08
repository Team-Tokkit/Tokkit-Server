package com.example.Tokkit_server.user.service;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.user.utils.MultipartInputResource;
import com.example.Tokkit_server.user.utils.OcrProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final OcrProperties ocrProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> extractInfo(MultipartFile image) {
        String url = ocrProperties.getIdcard().getInvokeUrl();
        String secretKey = ocrProperties.getIdcard().getSecretKey();

        try {
            // 1. OCR API 요청 메시지 구성
            Map<String, Object> messagePayload = new HashMap<>();
            messagePayload.put("version", "V2");
            messagePayload.put("requestId", UUID.randomUUID().toString());
            messagePayload.put("timestamp", System.currentTimeMillis());

            Map<String, String> imageInfo = new HashMap<>();
            imageInfo.put("format", "jpg");
            imageInfo.put("name", "demo");
            messagePayload.put("images", List.of(imageInfo));
            String messageJson = objectMapper.writeValueAsString(messagePayload);

            // 2. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            // 3. 바디 설정
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputResource(image));
            body.add("message", messageJson);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
            }

            // 5. 응답 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dlNode = root.path("images").get(0).path("idCard").path("result").path("dl");

            String name = getFirstFormattedValue(dlNode, "name");
            String rrn = getFirstFormattedValue(dlNode, "personalNum");
            String issuedDate = extractIssuedDate(dlNode);

            // 디버깅용 출력
            System.out.println("== OCR 결과 ==");
            System.out.println("이름: " + name);
            System.out.println("주민번호: " + rrn);
            System.out.println("발급일자: " + issuedDate);

            Map<String, String> result = new HashMap<>();
            result.put("name", name);
            result.put("rrn", rrn);
            result.put("issuedDate", issuedDate);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * formatted.value 추출 보조 메서드
     */
    private String getFirstFormattedValue(JsonNode node, String field) {
        JsonNode arr = node.path(field);
        if (arr.isArray() && arr.size() > 0) {
            JsonNode formatted = arr.get(0).path("formatted");
            if (formatted != null && formatted.has("value")) {
                return formatted.get("value").asText(null);
            }
        }
        return null;
    }

    /**
     * 발급일자 추출: year 누락 시 condition에서 보완, month/day 누락 시 text에서 보완
     */
    private String extractIssuedDate(JsonNode dlNode) {
        JsonNode issueDateNode = dlNode.path("issueDate");
        JsonNode conditionNode = dlNode.path("condition");

        if (!issueDateNode.isArray() || issueDateNode.size() == 0) return null;

        JsonNode formatted = issueDateNode.get(0).path("formatted");
        String year = formatted.has("year") ? formatted.get("year").asText() : "";
        String month = formatted.has("month") ? formatted.get("month").asText() : "";
        String day = formatted.has("day") ? formatted.get("day").asText() : "";

        // 연도 누락 시 condition.text에서 4자리 연도 추출
        if (year.isBlank() && conditionNode.isArray() && conditionNode.size() > 0) {
            String conditionText = conditionNode.get(0).path("text").asText();
            Matcher matcher = Pattern.compile("\\b(19|20)\\d{2}\\b").matcher(conditionText);
            if (matcher.find()) {
                year = matcher.group();
            }
        }

        // 월, 일 누락 시 text 필드에서 추출 (예: "4 .05.09")
        if (month.isBlank() || day.isBlank()) {
            String issueText = issueDateNode.get(0).path("text").asText("");
            Matcher matcher = Pattern.compile("(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])").matcher(issueText);
            if (matcher.find()) {
                String[] parts = matcher.group().split("\\.");
                if (month.isBlank()) month = parts[0];
                if (day.isBlank()) day = parts[1];
            }
        }

        if (!year.isBlank() && !month.isBlank() && !day.isBlank()) {
            return year + "." + month + "." + day;
        }
        return null;
    }
}
