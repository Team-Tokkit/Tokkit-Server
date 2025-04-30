package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/ocr")
public class OcrController {

	private final OcrService ocrService;

	@PostMapping(value = "/verify-identity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<?> verifyIdentity(
			@RequestParam("name") String name,
			@RequestParam("rrnPrefix") String rrnPrefix,
			@RequestParam("issuedDate") String issuedDate,
			@RequestPart("image") MultipartFile image
	) {
		try {
			Map<String, String> ocr = ocrService.extractInfo(image);

			if (ocr.get("name") == null || ocr.get("rrn") == null || ocr.get("issuedDate") == null)
				throw new GeneralException(ErrorStatus._BAD_REQUEST);

			boolean match = ocr.get("name").equals(name) &&
					ocr.get("rrn").startsWith(rrnPrefix) &&
					ocr.get("issuedDate").equals(issuedDate);

			if (!match) {
				return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), "입력값과 신분증 정보가 일치하지 않습니다.", null);
			}

			return ApiResponse.onSuccess("본인 확인 완료");

		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}