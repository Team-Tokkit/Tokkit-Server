package com.example.Tokkit_server.service;

import com.example.Tokkit_server.config.OcrProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OcrService {

	private final OcrProperties ocrProperties;

	public Map<String, String> extractInfo(MultipartFile file) throws Exception {
		String SECRET_KEY = ocrProperties.getSecretKey();
		String INVOKE_URL = ocrProperties.getInvokeUrl();

		byte[] imageBytes = file.getBytes();
		String base64Image = Base64.encodeBase64String(imageBytes);

		Map<String, Object> request = new LinkedHashMap<>();
		request.put("version", "V2");
		request.put("requestId", UUID.randomUUID().toString());
		request.put("timestamp", System.currentTimeMillis());

		Map<String, Object> imageMap = new HashMap<>();
		imageMap.put("format", "jpg");
		imageMap.put("name", "ocr_image");
		imageMap.put("data", base64Image);
		request.put("images", List.of(imageMap));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-OCR-SECRET", SECRET_KEY);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(INVOKE_URL, entity, String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode fields = mapper.readTree(response.getBody())
			.path("images").get(0).path("fields");


		// 신분증 종류 판단 (상위 5줄 합쳐서 보기)
		String cardType = "unknown";
		StringBuilder topTextBuilder = new StringBuilder();
		for (int i = 0; i < Math.min(5, fields.size()); i++) {
			topTextBuilder.append(fields.get(i).path("inferText").asText());
		}
		String topText = topTextBuilder.toString();
		if (topText.contains("주민등록증")) cardType = "idCard";
		else if (topText.contains("운전면허증") || topText.contains("자동차운전면허증")) cardType = "driver";

		System.out.println("\n[카드 종류 판단 결과]: " + cardType);

		Map<String, String> result;
		if ("idCard".equals(cardType)) {
			result = extractFromIdCard(fields);
		} else if ("driver".equals(cardType)) {
			result = extractFromDriverLicense(fields);
		} else {
			throw new IllegalArgumentException("신분증 종류를 인식할 수 없습니다.");
		}

		System.out.println("\n===== [최종 추출 결과] =====");
		result.forEach((k, v) -> System.out.println(k + ": " + v));
		return result;
	}

	private Map<String, String> extractFromIdCard(JsonNode fields) {
		return extractCommon(fields, "idCard");
	}

	private Map<String, String> extractFromDriverLicense(JsonNode fields) {
		return extractCommon(fields, "driver");
	}

	private Map<String, String> extractCommon(JsonNode fields, String type) {
		String name = null, rrn = null, issuedDate = null;
		List<String> dateCandidates = new ArrayList<>();

		for (int i = 0; i < fields.size(); i++) {
			String text = fields.get(i).path("inferText").asText();

			// 주민번호 찾기
			if (rrn == null && text.matches("\\d{6}-\\d{7}")) {
				rrn = text;
				System.out.println("[주민번호 찾음]: " + rrn);
				for (int j = i - 1; j >= 0 && j >= i - 2; j--) {
					String candidate = fields.get(j).path("inferText").asText();
					String cleaned = candidate.replaceAll("[^가-힣]", "");
					if (cleaned.matches("^[가-힣]{2,4}$")) {
						name = cleaned;
						System.out.println("[이름 선택]: " + name);
						break;
					}
				}
			}

			// 단일 항목에서 날짜 인식
			if (text.matches(".*\\d{2,4}[./년]\\d{1,2}[./월]\\d{1,2}.*")) {
				String datePart = text.replaceAll(".*?(\\d{2,4}[./년]\\d{1,2}[./월]\\d{1,2}).*", "$1");
				String normalized = normalizeDate(datePart);
				if (normalized != null) {
					dateCandidates.add(normalized);
				}
			}

			// 슬라이딩 윈도우 방식으로 날짜 조합 시도
			if (i + 2 < fields.size()) {
				String d1 = fields.get(i).path("inferText").asText();
				String d2 = fields.get(i + 1).path("inferText").asText();
				String d3 = fields.get(i + 2).path("inferText").asText();

				String merged = (d1 + "." + d2 + "." + d3).replaceAll("[^0-9.]", "");
				String normalized = normalizeDate(merged);
				if (normalized != null) {
					dateCandidates.add(normalized);
				}
			}
		}

		// 가장 마지막 후보를 발급일자로 사용
		for (int k = dateCandidates.size() - 1; k >= 0; k--) {
			String date = dateCandidates.get(k);
			if (date.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
				issuedDate = date;
				System.out.println("[발급일자 선택]: " + issuedDate);
				break;
			}
		}

		return Map.of(
			"name", name != null ? name : "이름 미인식",
			"rrn", rrn != null ? rrn : "주민번호 미인식",
			"issuedDate", issuedDate != null ? issuedDate : "발급일자 미인식"
		);
	}

	private String normalizeDate(String dateStr) {
		if (dateStr == null || dateStr.isBlank()) return null;

		dateStr = dateStr.trim().replaceAll("[\\s]+", "")
			.replaceAll("[.]{2,}", ".")
			.replaceAll("[^0-9./년월일-]", "");
		if (dateStr.endsWith(".")) {
			dateStr = dateStr.substring(0, dateStr.length() - 1);
		}

		String[] patterns = {
			"yyyy.MM.dd", "yyyy.M.d", "yy.MM.dd", "yy.M.d",
			"yyyy-MM-dd", "yy-MM-dd",
			"yyyy/MM/dd", "yy/MM/dd",
			"yyyy년MM월dd일", "yy년MM월dd일"
		};

		for (String pattern : patterns) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				Date date = sdf.parse(dateStr);
				return new SimpleDateFormat("yyyy.MM.dd").format(date);
			} catch (ParseException ignored) {
			}
		}
		return null;
	}
}
