package com.example.Tokkit_server.merchant.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.merchant.dto.response.BusinessOcrResponseDto;
import com.example.Tokkit_server.merchant.service.BusinessOcrService;
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
public class BusinessOcrController {

private final BusinessOcrService businessOcrService;

@PostMapping(value = "/business", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(summary = "사업자등록증 OCR", description = "이미지를 업로드하면 사업자번호, 상호명, 주소를 추출합니다.")
public ApiResponse<BusinessOcrResponseDto> extractBusinessInfo(@RequestParam MultipartFile image) {
    return ApiResponse.onSuccess(businessOcrService.extractBusinessInfo(image));
}
}
