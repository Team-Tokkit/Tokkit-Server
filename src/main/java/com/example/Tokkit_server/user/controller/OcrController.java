package com.example.Tokkit_server.user.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.user.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/ocr")
@Tag(name = "OCR", description = "OCR 관련 API입니다.")
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(value = "/verify-identity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "OCR 인식 요청", description = "요청으로 들어온 값과 이미지에서 OCR로 추출한 값을 비교합니다.")
    public ApiResponse<?> verifyIdentity(
            @RequestParam("name") String name,
            @RequestParam("rrnPrefix") String rrnPrefix,
            @RequestParam("issuedDate") String issuedDate,
            @RequestPart("image") MultipartFile image
    ) {
        try {
            Map<String, String> ocr = ocrService.extractInfo(image);
            System.out.println("OCR 추출 결과: " + ocr);

            String extractedName = ocr.get("name");
            String extractedRrn = ocr.get("rrn");
            String extractedIssuedDate = ocr.get("issuedDate");

            // 어떤 값이 null인지 명시적으로 확인
            if (extractedName == null || extractedRrn == null || extractedIssuedDate == null) {
                return ApiResponse.onFailure(
                        ErrorStatus._BAD_REQUEST.getCode(),
                        "OCR로부터 필요한 정보를 모두 추출하지 못했습니다.",
                        ocr  // 👉 추출된 값 그대로 반환
                );
            }

            // 값 비교
            boolean match = extractedName.equals(name)
                    && extractedRrn.startsWith(rrnPrefix)
                    && extractedIssuedDate.equals(issuedDate);

            if (!match) {
                return ApiResponse.onFailure(
                        ErrorStatus._BAD_REQUEST.getCode(),
                        "입력값과 신분증 정보가 일치하지 않습니다.",
                        ocr
                );
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