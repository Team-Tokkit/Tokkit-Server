package com.example.Tokkit_server.ocr.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.ocr.dto.BusinessOcrResponseDto;
import com.example.Tokkit_server.ocr.dto.IdCardOcrResponseDto;
import com.example.Tokkit_server.ocr.service.BusinessOcrService;
import com.example.Tokkit_server.ocr.service.IdCardOcrService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final BusinessOcrService businessOcrService;
    private final IdCardOcrService idCardOcrService;

    @PostMapping(value = "/business", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "사업자등록증 OCR", description = "이미지를 업로드하면 사업자번호, 상호명, 주소를 추출합니다.")
    public ApiResponse<BusinessOcrResponseDto> extractBusinessInfo(@RequestParam MultipartFile image) {
        return ApiResponse.onSuccess(businessOcrService.extractBusinessInfo(image));
    }

    @PostMapping(value = "/idCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "신분증 OCR", description = "이름, 주민등록번호 앞 7자리, 발급일자를 추출합니다.")
    public ApiResponse<IdCardOcrResponseDto> extractIdCardInfo(@RequestParam MultipartFile image) {
        return ApiResponse.onSuccess(idCardOcrService.extractInfo(image));
    }
}
